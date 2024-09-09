package com.homefit.backend.exercise.service;

import com.homefit.backend.exercise.dto.ExerciseRequest;
import com.homefit.backend.exercise.dto.ExerciseResponse;
import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.service.ExerciseCategoryService;
import com.homefit.backend.exerciselog.service.ExerciseLogService;
import com.homefit.backend.global.exception.ErrorCode;
import com.homefit.backend.global.exception.model.ConflictException;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCategoryService exerciseCategoryService;
    private final ExerciseLogService exerciseLogService;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository, ExerciseCategoryService exerciseCategoryService, ExerciseLogService exerciseLogService) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseCategoryService = exerciseCategoryService;
        this.exerciseLogService = exerciseLogService;
    }

    // 운동 이름 유효성 검사
    public void validateExerciseName(String exerciseName) {
        log.info("운동 이름 유효성 검사 시작: {}", exerciseName);
        if (exerciseName == null || exerciseName.isEmpty()) {
            log.error("유효성 검사 실패: 운동 이름이 null이거나 비어 있음");
            throw new ValidationException("운동 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }

        boolean exists = exerciseRepository.existsByExerciseName(exerciseName);
        if (exists) {
            log.error("운동 이름 중복: {}", exerciseName);
            throw new ConflictException("이미 존재하는 운동 이름입니다: " + exerciseName, ErrorCode.CONFLICT_EXCEPTION);
        }
        log.info("운동 이름 유효성 검사 통과: {}", exerciseName);
    }


    // 운동 생성
    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest exerciseRequest) {
        log.info("운동 생성 요청: {}", exerciseRequest.getExerciseName());

        // 운동 이름 유효성 검사
        validateExerciseName(exerciseRequest.getExerciseName());

        // ExerciseCategoryService에서 카테고리 조회 (엔티티)
        ExerciseCategory exerciseCategory = exerciseCategoryService.findCategoryEntityById(exerciseRequest.getExerciseCategoryId());

        // 운동 카테고리가 null인 경우 유효성 검사 실패 처리
        if (exerciseCategory == null) {
            log.error("운동 카테고리가 null입니다.");
            throw new ValidationException("운동 카테고리는 필수 입력 사항입니다.");
        }

        // 새로운 운동 생성 및 저장
        Exercise exercise = new Exercise(exerciseRequest.getExerciseName(), exerciseCategory);
        Exercise savedExercise = exerciseRepository.save(exercise);

        log.info("운동 생성 완료: ID={}", savedExercise.getId());
        return mapToExerciseResponse(savedExercise);
    }


    // 운동 수정
    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest) {
        log.info("운동 수정 요청: ID={}, 새 이름={}", id, exerciseRequest.getExerciseName());

        // 운동 존재 여부 확인
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다."));

        // 운동 이름 수정 시 유효성 검사
        validateUpdateExercise(exerciseRequest.getExerciseName(), existingExercise);

        // 새로운 카테고리 확인 (엔티티)
        ExerciseCategory newCategory = exerciseCategoryService.findCategoryEntityById(exerciseRequest.getExerciseCategoryId());

        // 카테고리가 null인 경우 예외 처리
        if (newCategory == null) {
            log.error("운동 카테고리가 null입니다.");
            throw new ValidationException("운동 카테고리는 null이 될 수 없습니다.");
        }

        // 운동 정보 업데이트
        existingExercise.updateExercise(exerciseRequest.getExerciseName(), newCategory);
        Exercise updatedExercise = exerciseRepository.save(existingExercise);

        log.info("운동 수정 완료: ID={}", updatedExercise.getId());
        return mapToExerciseResponse(updatedExercise);
    }

    // 운동 수정 시 유효성 검사
    public void validateUpdateExercise(String newName, Exercise existingExercise) {
        log.info("운동 수정 유효성 검사 시작: 기존 이름={}, 새 이름={}", existingExercise.getExerciseName(), newName);

        if (newName == null || newName.isEmpty()) {
            log.error("유효성 검사 실패: 새 운동 이름이 null이거나 비어 있음");
            throw new ValidationException("새 운동 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }

        if (existingExercise.getExerciseName().equals(newName)) {
            log.error("유효성 검사 실패: 새 이름이 기존 이름과 동일함");
            throw new ConflictException("새 운동 이름은 기존 이름과 같을 수 없습니다.", ErrorCode.CONFLICT_EXCEPTION);
        }

        boolean exists = exerciseRepository.existsByExerciseName(newName);
        if (exists) {
            log.error("유효성 검사 실패: 동일한 이름의 운동이 이미 존재함: {}", newName);
            throw new ConflictException("이미 존재하는 운동 이름입니다: " + newName, ErrorCode.CONFLICT_EXCEPTION);
        }

        log.info("운동 수정 유효성 검사 통과: 새 이름={}", newName);
    }

    // 운동 삭제
    @Transactional
    public void deleteExercise(Long id) {
        log.info("운동 삭제 요청: ID={}", id);

        // 운동 존재 여부 확인
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다."));

        // 운동과 관련된 로그가 있는지 확인 (ExerciseLogService 사용)
        boolean hasLogs = exerciseLogService.existsByExercise(exercise);  // 서비스로 처리
        if (hasLogs) {
            throw new ValidationException("이 운동은 운동 로그에 사용되고 있으므로 삭제할 수 없습니다.");
        }

        // 운동 삭제
        exerciseRepository.delete(exercise);
        log.info("운동 삭제 완료: ID={}", id);
    }

    // 특정 운동 조회
    public ExerciseResponse getExerciseById(Long id) {
        log.info("특정 운동 조회 요청: ID={}", id);

        // 운동 존재 여부 확인
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("운동을 찾을 수 없습니다."));

        return mapToExerciseResponse(exercise);
    }

    // 전체 운동 조회 (필터링 기능 추가)
    public List<ExerciseResponse> getAllExercises(Optional<String> categoryName) {
        log.info("필터링된 전체 운동 조회 요청");

        List<Exercise> exercises;
        if (categoryName.isPresent()) {
            // 카테고리 이름으로 ExerciseCategory를 조회한 후, 해당 카테고리의 운동을 가져옴
            ExerciseCategory category = exerciseCategoryService.findCategoryEntityByName(categoryName.get());
            exercises = exerciseRepository.findByExerciseCategory(category);
        } else {
            exercises = exerciseRepository.findAll();
        }

        List<ExerciseResponse> exerciseResponses = exercises.stream()
                .map(this::mapToExerciseResponse)
                .collect(Collectors.toList());

        log.info("전체 운동 조회 완료, 총 개수: {}", exercises.size());
        return exerciseResponses;
    }

    // Exercise 엔티티를 ExerciseResponse DTO로 변환하는 로직
    private ExerciseResponse mapToExerciseResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .exerciseName(exercise.getExerciseName())
                .exerciseCategoryName(exercise.getExerciseCategory().getName()) // 카테고리 이름
                .createdAt(exercise.getCreatedAt())  // 생성 시간
                .updatedAt(exercise.getUpdatedAt())  // 수정 시간
                .build();
    }



}
