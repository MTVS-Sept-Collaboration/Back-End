package com.homefit.backend.exerciselog.service;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.exerciselog.repository.ExerciseLogRepository;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        return exerciseLogRepository.existsByExercisesContaining(exercise);
    }

    // 운동 기록 생성
    @Transactional
    public ExerciseLogResponse createExerciseLog(ExerciseLogRequest request) {
        log.info("운동 기록 생성 요청: userId={}, date={}, caloriesBurned={}, exerciseCount={}, exerciseTime={}",
                request.getUserId(), request.getDate(), request.getCaloriesBurned(), request.getExerciseCount(), request.getExerciseTime());

        // 입력값 유효성 검사
        validateExerciseLogRequest(request);

        // 유저 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("운동 기록 생성 실패: 유저를 찾을 수 없습니다. userId={}", request.getUserId());
                    return new NotFoundException("유저를 찾을 수 없습니다. ID=" + request.getUserId());
                });

        log.info("운동 기록 생성: 유저 확인 완료, userId={}", user.getId());

        // 운동 리스트 확인
        List<Exercise> exercises = exerciseRepository.findAllById(request.getExerciseIds());
        if (exercises.size() != request.getExerciseIds().size()) {
            log.error("운동 기록 생성 실패: 유효하지 않은 운동 ID가 포함되어 있습니다. 요청된 운동 IDs={}", request.getExerciseIds());
            throw new ValidationException("유효하지 않은 운동 ID가 포함되어 있습니다.");
        }

        log.info("운동 기록 생성: 운동 리스트 확인 완료, exerciseIds={}", request.getExerciseIds());

        // 새로운 운동 기록 생성 및 저장
        ExerciseLog exerciseLog = new ExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                request.getExerciseTime(),
                user
        );
        exerciseLog.getExercises().addAll(exercises);
        ExerciseLog savedLog = exerciseLogRepository.save(exerciseLog);

        log.info("운동 기록 생성 완료: ID={}, userId={}, exerciseCount={}, caloriesBurned={}", savedLog.getId(), user.getId(), savedLog.getExerciseCount(), savedLog.getCaloriesBurned());
        return mapToExerciseLogResponse(savedLog);
    }

    // 운동 기록 수정 (생성한 유저만 수정 가능)
    @Transactional
    public ExerciseLogResponse updateExerciseLog(Long id, Long userId, ExerciseLogRequest request) {
        log.info("운동 기록 수정 요청: ID={}, userId={}, date={}, caloriesBurned={}, exerciseCount={}",
                id, userId, request.getDate(), request.getCaloriesBurned(), request.getExerciseCount());

        // 운동 기록 확인
        ExerciseLog existingLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("운동 기록 수정 실패: 운동 기록을 찾을 수 없습니다. ID={}", id);
                    return new NotFoundException("운동 기록을 찾을 수 없습니다. ID=" + id);
                });

        // 유저 확인
        if (!existingLog.getUser().getId().equals(userId)) {
            log.error("운동 기록 수정 권한 없음: userId={}, 기록 생성 유저 ID={}", userId, existingLog.getUser().getId());
            throw new ValidationException("운동 기록 수정 권한이 없습니다.");
        }

        log.info("운동 기록 수정: 유저 확인 완료, userId={}", userId);

        // 운동 리스트 확인
        List<Exercise> exercises = exerciseRepository.findAllById(request.getExerciseIds());
        if (exercises.size() != request.getExerciseIds().size()) {
            log.error("운동 기록 수정 실패: 유효하지 않은 운동 ID가 포함되어 있습니다. 요청된 운동 IDs={}", request.getExerciseIds());
            throw new ValidationException("유효하지 않은 운동 ID가 포함되어 있습니다.");
        }

        log.info("운동 기록 수정: 운동 리스트 확인 완료, exerciseIds={}", request.getExerciseIds());

        // 운동 기록 업데이트
        existingLog.updateExerciseLog(
                request.getDate(),
                request.getCaloriesBurned(),
                request.getExerciseCount(),
                request.getExerciseTime()
        );
        existingLog.getExercises().clear();
        existingLog.getExercises().addAll(exercises);

        ExerciseLog updatedLog = exerciseLogRepository.save(existingLog);

        log.info("운동 기록 수정 완료: ID={}, userId={}, updatedExerciseCount={}, updatedCaloriesBurned={}",
                updatedLog.getId(), userId, updatedLog.getExerciseCount(), updatedLog.getCaloriesBurned());

        return mapToExerciseLogResponse(updatedLog);
    }

    // 운동 기록 삭제 (생성한 유저만 삭제 가능)
    @Transactional
    public void deleteExerciseLog(Long id, Long userId) {
        log.info("운동 기록 삭제 요청: ID={}, 유저 ID={}", id, userId);

        // 운동 기록 확인
        ExerciseLog exerciseLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동 기록을 찾을 수 없습니다."));

        // 유저 확인 (기록 생성 유저와 요청 유저가 같은지 확인)
        if (!exerciseLog.getUser().getId().equals(userId)) {
            log.error("운동 기록 삭제 권한 없음: 유저 ID={}, 기록 생성 유저 ID={}", userId, exerciseLog.getUser().getId());
            throw new ValidationException("운동 기록 삭제 권한이 없습니다.");
        }

        exerciseLogRepository.delete(exerciseLog);
        log.info("운동 기록 삭제 완료: ID={}", id);
    }

    // 특정 유저의 운동 기록만 조회
    public List<ExerciseLogResponse> getExerciseLogsByUser(Long userId) {
        log.info("유저 ID={}의 운동 기록 조회 요청", userId);

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + userId));

        // 해당 유저의 운동 기록 조회
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUser(user);
        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }

    // 특정 날짜에 해당하는 유저의 운동 기록 조회
    public List<ExerciseLogResponse> getExerciseLogsByUserAndDate(Long userId, LocalDate date) {
        log.info("특정 유저의 특정 날짜 운동 기록 조회 요청: userId={}, date={}", userId, date);

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다. ID=" + userId));

        // 특정 날짜의 운동 기록 조회 (해당 날짜의 00:00 ~ 23:59:59 범위)
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 해당 유저의 특정 날짜 운동 기록 조회
        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByUserAndDateBetween(user, startOfDay, endOfDay);

        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }


    // 특정 운동 기록 조회
    public ExerciseLogResponse getExerciseLogById(Long id) {
        log.info("운동 기록 조회 요청: ID={}", id);

        ExerciseLog exerciseLog = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동 기록을 찾을 수 없습니다. ID=" + id));
        return mapToExerciseLogResponse(exerciseLog);
    }

    // 전체 운동 기록 조회
    public List<ExerciseLogResponse> getAllExerciseLogs() {
        log.info("전체 운동 기록 조회 요청");

        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findAll();
        return exerciseLogs.stream()
                .map(this::mapToExerciseLogResponse)
                .collect(Collectors.toList());
    }

    // ExerciseLog 엔티티를 ExerciseLogResponse로 변환
    private ExerciseLogResponse mapToExerciseLogResponse(ExerciseLog exerciseLog) {
        List<String> exerciseNames = exerciseLog.getExercises().stream()
                .map(Exercise::getExerciseName)
                .collect(Collectors.toList());

        return ExerciseLogResponse.builder()
                .id(exerciseLog.getId())
                .date(exerciseLog.getDate())
                .caloriesBurned(exerciseLog.getCaloriesBurned())
                .exerciseCount(exerciseLog.getExerciseCount())
                .exerciseTime(exerciseLog.getExerciseTime())
                .exerciseNames(exerciseNames)
                .createdAt(exerciseLog.getCreatedAt())
                .updatedAt(exerciseLog.getUpdatedAt())
                .build();
    }

    // 운동 기록 요청 데이터 유효성 검사
    private void validateExerciseLogRequest(ExerciseLogRequest request) {
        if (request.getCaloriesBurned() != null && request.getCaloriesBurned() <= 0) {
            log.error("유효성 검사 실패: 소모된 칼로리가 0 이하입니다. 입력값={}", request.getCaloriesBurned());
            throw new ValidationException("소모된 칼로리는 0보다 커야 합니다.");
        }

        if (request.getExerciseCount() != null && request.getExerciseCount() <= 0) {
            log.error("유효성 검사 실패: 운동 횟수가 0 이하입니다. 입력값={}", request.getExerciseCount());
            throw new ValidationException("운동 횟수는 0보다 커야 합니다.");
        }

        if (request.getExerciseTime() != null && request.getExerciseTime().isNegative()) {
            log.error("유효성 검사 실패: 운동 시간이 음수입니다. 입력값={}", request.getExerciseTime());
            throw new ValidationException("운동 시간은 음수가 될 수 없습니다.");
        }

        if (request.getUserId() == null) {
            log.error("유효성 검사 실패: 유저 ID가 제공되지 않았습니다.");
            throw new ValidationException("유저 ID는 필수 입력 사항입니다.");
        }

        if (request.getExerciseIds() == null || request.getExerciseIds().isEmpty()) {
            log.error("유효성 검사 실패: 운동 ID 리스트가 비어 있습니다.");
            throw new ValidationException("운동 ID 리스트는 비어 있을 수 없습니다.");
        }
    }

}
