package com.homefit.backend.exerciselog.service;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.dto.TotalExerciseLogResponse;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.exerciselog.repository.ExerciseLogRepository;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseLogService {

    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseLogService(ExerciseLogRepository exerciseLogRepository, UserRepository userRepository, ExerciseRepository exerciseRepository) {
        this.exerciseLogRepository = exerciseLogRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public boolean existsByExercise(Exercise exercise) {
        return exerciseLogRepository.existsByExercise(exercise);
    }

    // 운동 기록 조회
    public ExerciseLogResponse getExerciseLogById(Long id, Long userId) {
        log.info("운동 기록 조회 요청: ID={}, userId={}", id, userId);

        // 운동 기록 확인
        ExerciseLog exerciseLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("운동 기록을 찾을 수 없습니다. ID={}", id);
                    return new NotFoundException("운동 기록을 찾을 수 없습니다. ID=" + id);
                });

        // 유저 확인 (기록 생성 유저와 요청 유저가 같은지 확인)
        if (!exerciseLog.getUser().getId().equals(userId)) {
            log.error("운동 기록 조회 권한 없음: userId={}, 기록 생성 유저 ID={}", userId, exerciseLog.getUser().getId());
            throw new ValidationException("운동 기록 조회 권한이 없습니다.");
        }

        return mapToExerciseLogResponse(exerciseLog);
    }

    // 관리자 전체 운동 기록 조회
    @PreAuthorize("hasRole('ADMIN')")
    public List<ExerciseLogResponse> getAllExerciseLogs() {
        log.info("관리자에 의한 전체 운동 기록 조회 요청");

        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findAll();
        if (exerciseLogs.isEmpty()) {
            log.warn("운동 기록이 존재하지 않습니다.");
        }

        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }

    // 운동 기록 생성
    @Transactional
    public ExerciseLogResponse createExerciseLog(ExerciseLogRequest request) {
        log.info("운동 기록 생성 요청: userId={}, date={}, caloriesBurned={}, exerciseCount={}, startTime={}, endTime={}",
                request.getUserId(), request.getDate(), request.getCaloriesBurned(), request.getExerciseCount(), request.getStartTime(), request.getEndTime());

        // 유효성 검사
        validateExerciseLogRequest(request);

        // 유저 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + request.getUserId()));

        // 운동 확인
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다. ID=" + request.getExerciseId()));

        // 운동 기록 생성 및 저장
        ExerciseLog exerciseLog = new ExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                request.getStartTime(),
                request.getEndTime(),
                user,
                exercise
        );

        ExerciseLog savedLog = exerciseLogRepository.save(exerciseLog);

        // 운동 기록 생성 시 로그 기록
        log.info("유저 ID={}가 운동을 완료했습니다. 운동명={}, 운동 횟수={}, 소모 칼로리={}",
                user.getId(), exercise.getExerciseName(), request.getExerciseCount(), request.getCaloriesBurned());

        log.info("운동 기록 생성 완료: ID={}, userId={}, exerciseCount={}, caloriesBurned={}",
                savedLog.getId(), user.getId(), savedLog.getExerciseCount(), savedLog.getCaloriesBurned());

        return mapToExerciseLogResponse(savedLog);
    }

    // 운동 기록 수정
    @Transactional
    public ExerciseLogResponse updateExerciseLog(Long id, Long userId, ExerciseLogRequest request) {
        log.info("운동 기록 수정 요청: ID={}, userId={}, date={}, caloriesBurned={}, exerciseCount={}, startTime={}, endTime={}",
                id, userId, request.getDate(), request.getCaloriesBurned(), request.getExerciseCount(), request.getStartTime(), request.getEndTime());

        // 운동 기록 확인
        ExerciseLog existingLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동 기록을 찾을 수 없습니다. ID=" + id));

        // 권한 확인
        if (!existingLog.getUser().getId().equals(userId)) {
            throw new ValidationException("운동 기록 수정 권한이 없습니다.");
        }

        // 운동 확인
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다. ID=" + request.getExerciseId()));

        // 운동 기록 업데이트
        existingLog.updateExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                request.getStartTime(),
                request.getEndTime()
        );

        existingLog.setExercise(exercise);

        ExerciseLog updatedLog = exerciseLogRepository.save(existingLog);

        // 운동 기록 수정 시 로그 기록
        log.info("유저 ID={}가 운동 기록을 수정했습니다. 운동명={}, 수정된 횟수={}, 수정된 소모 칼로리={}",
                userId, exercise.getExerciseName(), request.getExerciseCount(), request.getCaloriesBurned());

        log.info("운동 기록 수정 완료: ID={}, userId={}, updatedExerciseCount={}, updatedCaloriesBurned={}",
                updatedLog.getId(), userId, updatedLog.getExerciseCount(), updatedLog.getCaloriesBurned());

        return mapToExerciseLogResponse(updatedLog);
    }

    // 운동 기록 삭제
    @Transactional
    public void deleteExerciseLog(Long id, Long userId) {
        log.info("운동 기록 삭제 요청: ID={}, 유저 ID={}", id, userId);

        // 운동 기록 확인
        ExerciseLog exerciseLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("운동 기록을 찾을 수 없습니다. ID={}", id);
                    return new NotFoundException("운동 기록을 찾을 수 없습니다.");
                });

        // 유저 확인 (기록 생성 유저와 요청 유저가 같은지 확인)
        if (!exerciseLog.getUser().getId().equals(userId)) {
            log.error("운동 기록 삭제 권한 없음: 유저 ID={}, 기록 생성 유저 ID={}", userId, exerciseLog.getUser().getId());
            throw new ValidationException("운동 기록 삭제 권한이 없습니다.");
        }

        exerciseLogRepository.delete(exerciseLog);
        log.info("운동 기록 삭제 완료: ID={}", id);
    }

    // 특정 유저의 운동 기록 조회
    public List<ExerciseLogResponse> getExerciseLogsByUser(Long userId) {
        log.info("유저 ID={}의 운동 기록 조회 요청", userId);

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + userId));

        // 해당 유저의 운동 기록 조회
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUser(user);

        // 유저별 운동 횟수 조회 로그 기록
        log.info("유저 ID={}가 조회한 총 운동 횟수: {}", userId, exerciseLogs.stream().mapToInt(ExerciseLog::getExerciseCount).sum());

        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }

    // 특정 날짜에 해당하는 유저의 운동 기록 조회
    public TotalExerciseLogResponse getTotalExerciseLogsByUserAndDate(Long userId, LocalDate date) {
        log.info("특정 유저의 특정 날짜 운동 기록 조회 요청 (합산 결과): userId={}, date={}", userId, date);

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + userId));

        // 특정 날짜의 유저의 운동 기록을 조회 (해당 날짜의 운동 로그)
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUserAndDateBetween(user, date, date);

        // 각 운동 기록을 로그로 남기기
        for (ExerciseLog exerciseLog : exerciseLogs) {
            log.info("유저 ID={}가 {}일에 수행한 운동: 운동명={}, 횟수={}, 소모 칼로리={}",
                    userId, date, exerciseLog.getExercise().getExerciseName(), exerciseLog.getExerciseCount(), exerciseLog.getCaloriesBurned());
        }

        // 총 칼로리 소모와 총 운동 횟수를 계산
        double totalCalories = exerciseLogs.stream()
                .mapToDouble(ExerciseLog::getCaloriesBurned)
                .sum(); // 모든 칼로리 소모 합산

        int totalExerciseCount = exerciseLogs.stream()
                .mapToInt(ExerciseLog::getExerciseCount)
                .sum(); // 모든 운동 횟수 합산

        // 운동 로그 조회 로그 기록 (총합 결과)
        log.info("유저 ID={}가 {}일에 수행한 총 운동 횟수: {}, 총 소모 칼로리: {}", userId, date, totalExerciseCount, totalCalories);

        // 새로운 TotalExerciseLogResponse로 반환
        return TotalExerciseLogResponse.builder()
                .date(date)
                .totalCaloriesBurned(totalCalories)
                .totalExerciseCount(totalExerciseCount)
                .build();
    }


    // ExerciseLog 엔티티를 ExerciseLogResponse로 변환
    private ExerciseLogResponse mapToExerciseLogResponse(ExerciseLog exerciseLog) {
        return ExerciseLogResponse.builder()
                .id(exerciseLog.getId())
                .date(exerciseLog.getDate())
                .caloriesBurned(exerciseLog.getCaloriesBurned())
                .exerciseCount(exerciseLog.getExerciseCount())
                .exerciseId(exerciseLog.getExercise().getId())
                .build();
    }

    // 유효성 검사
    private void validateExerciseLogRequest(ExerciseLogRequest request) {
        if (request.getCaloriesBurned() != null && request.getCaloriesBurned() <= 0) {
            throw new ValidationException("소모된 칼로리는 0보다 커야 합니다.");
        }

        if (request.getExerciseCount() != null && request.getExerciseCount() <= 0) {
            throw new ValidationException("운동 횟수는 0보다 커야 합니다.");
        }

        if (request.getUserId() == null) {
            throw new ValidationException("유저 ID는 필수 입력 사항입니다.");
        }

        if (request.getExerciseId() == null) {
            throw new ValidationException("운동 ID는 필수 입력 사항입니다.");
        }
    }

}
