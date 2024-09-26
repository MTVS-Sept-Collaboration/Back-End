package com.homefit.backend.exerciselog.service;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.dto.TotalExerciseLogResponse;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.exerciselog.repository.ExerciseLogRepository;
import com.homefit.backend.exerciselog.util.TimeUtils;
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
import java.util.Optional;
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

    public boolean hasLogsForExercise(Long exerciseId) {
        return exerciseLogRepository.countByExerciseId(exerciseId) > 0;
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

        // 수동 변환 로직 추가
        LocalTime startTime = TimeUtils.convertStringToLocalTime(request.getStartTime());
        LocalTime endTime = TimeUtils.convertStringToLocalTime(request.getEndTime());

        // 운동 기록 생성 및 저장
        ExerciseLog exerciseLog = new ExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                startTime,  // 변환된 LocalTime 사용
                endTime,    // 변환된 LocalTime 사용
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

        // 운동 기록 확인 (특정 ID의 기록을 수정하는 것)
        ExerciseLog existingLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동 기록을 찾을 수 없습니다. ID=" + id));

        // 권한 확인 (수정 권한 확인)
        if (!existingLog.getUser().getId().equals(userId)) {
            throw new ValidationException("운동 기록 수정 권한이 없습니다.");
        }

        // 운동 확인
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다. ID=" + request.getExerciseId()));

        // 수동 변환 로직 추가 (시작 시간과 끝 시간)
        LocalTime startTime = TimeUtils.convertStringToLocalTime(request.getStartTime());
        LocalTime endTime = TimeUtils.convertStringToLocalTime(request.getEndTime());

        // 운동 기록 업데이트 (기존 기록 수정)
        existingLog.updateExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                startTime,  // 변환된 LocalTime 사용
                endTime    // 변환된 LocalTime 사용
        );
        existingLog.setExercise(exercise);

        ExerciseLog updatedLog = exerciseLogRepository.save(existingLog);

        // 운동 기록 수정 시 로그 기록
        log.info("유저 ID={}가 운동 기록을 수정했습니다. 운동명={}, 수정된 횟수={}, 수정된 소모 칼로리={}",
                userId, exercise.getExerciseName(), request.getExerciseCount(), request.getCaloriesBurned());

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

        // 해당 유저의 운동 기록 조회 (날짜별로 그룹화하지 않고 개별 기록을 그대로 조회)
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUser(user);

        // 유저별 운동 기록 조회 로그 기록
        log.info("유저 ID={}가 조회한 총 운동 기록 수: {}", userId, exerciseLogs.size());

        // 개별 운동 기록을 Response로 매핑하여 반환
        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }


    // 특정 날짜에 해당하는 유저의 운동 기록 조회
    public TotalExerciseLogResponse getTotalExerciseLogsByUserAndDate(Long userId, LocalDate date) {
        log.info("특정 유저의 특정 날짜 운동 기록 조회 요청: userId={}, date={}", userId, date);

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + userId));

        // 해당 유저의 특정 날짜 운동 기록 조회
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUserAndDateBetween(user, date, date);

        // 총 소모 칼로리 및 총 운동 기록 횟수 계산
        double totalCalories = exerciseLogs.stream()
                .mapToDouble(ExerciseLog::getCaloriesBurned)
                .sum();

        int totalExercisePerformances = exerciseLogs.size(); // 총 운동 수행 횟수 계산

        log.info("유저 ID={}가 {}일에 수행한 총 운동 기록 수: {}, 총 소모 칼로리: {}",
                userId, date, totalExercisePerformances, totalCalories);

        return TotalExerciseLogResponse.builder()
                .date(date)
                .totalCaloriesBurned(totalCalories)
                .totalExercisePerformances(totalExercisePerformances)
                .build();
    }






    /**
     * 운동 기록을 생성하거나 업데이트하는 메서드.
     * 동일한 운동 기록이 있어도 새로운 기록으로 추가하며, 소모 칼로리와 운동 횟수를 각각 저장.
     * 기존 기록을 확인하지 않고, 요청된 데이터를 바탕으로 새로운 운동 기록을 생성하고 저장.
     */
    @Transactional
    public ExerciseLogResponse createOrUpdateExerciseLog(ExerciseLogRequest request) {
        log.info("운동 기록 생성 요청: userId={}, date={}, exerciseId={}", request.getUserId(), request.getDate(), request.getExerciseId());

        // 유저 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + request.getUserId()));

        // 운동 확인
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다. ID=" + request.getExerciseId()));

        // 수동 변환 로직 추가
        LocalTime startTime = TimeUtils.convertStringToLocalTime(request.getStartTime());
        LocalTime endTime = TimeUtils.convertStringToLocalTime(request.getEndTime());

        // 새로운 운동 기록 생성
        ExerciseLog newLog = new ExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                startTime,
                endTime,
                user,
                exercise
        );

        ExerciseLog savedLog = exerciseLogRepository.save(newLog);
        log.info("새로운 운동 기록 생성: ID={}, userId={}, exerciseId={}, exerciseCount={}, caloriesBurned={}",
                savedLog.getId(), user.getId(), exercise.getId(), savedLog.getExerciseCount(), savedLog.getCaloriesBurned());

        return mapToExerciseLogResponse(savedLog);
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
