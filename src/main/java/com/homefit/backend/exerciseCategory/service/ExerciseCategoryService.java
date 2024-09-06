package com.homefit.backend.exerciseCategory.service;

import com.homefit.backend.exerciseCategory.dto.ExerciseCategoryRequest;
import com.homefit.backend.exerciseCategory.dto.ExerciseCategoryResponse;
import com.homefit.backend.exerciseCategory.entity.ExerciseCategory;
import com.homefit.backend.exerciseCategory.repository.ExerciseCategoryRepository;
import com.homefit.backend.global.exception.ErrorCode;
import com.homefit.backend.global.exception.model.ConflictException;
import com.homefit.backend.global.exception.model.NotFoundException;
import com.homefit.backend.global.exception.model.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseCategoryService {

    private final ExerciseCategoryRepository exerciseCategoryRepository;

    @Autowired
    public ExerciseCategoryService(ExerciseCategoryRepository exerciseCategoryRepository) {
        this.exerciseCategoryRepository = exerciseCategoryRepository;
    }

    public ExerciseCategory findCategoryEntityByName(String name) {
        return exerciseCategoryRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다. 이름: " + name));
    }

    // ExerciseCategory 엔티티를 반환하는 메서드 추가
    public ExerciseCategory findCategoryEntityById(Long id) {
        return exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다. ID: " + id));
    }

    // 카테고리 이름 유효성 검사
    public void validateCategoryName(String name) {
        log.info("카테고리 이름 유효성 검사 시작: {}", name);
        if (name == null || name.isEmpty()) {
            log.error("유효성 검사 실패: 카테고리 이름이 비어있음");
            throw new ValidationException("카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }
        boolean exists = exerciseCategoryRepository.existsByName(name);
        if (exists) {
            log.error("카테고리 이름 중복: {}", name);
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + name, ErrorCode.CONFLICT_EXCEPTION);
        }
    }

    // 카테고리 수정 시 유효성 검사
    public void validateUpdateCategory(String newName, ExerciseCategory exerciseCategory) {
        log.info("카테고리 수정 유효성 검사 시작: 기존 이름={}, 새 이름={}", exerciseCategory.getName(), newName);

        if (newName == null || newName.isEmpty()) {
            log.error("유효성 검사 실패: 새 카테고리 이름이 null이거나 비어 있음");
            throw new ValidationException("새 카테고리 이름은 null이거나 비어 있을 수 없습니다.", ErrorCode.VALIDATION_EXCEPTION);
        }

        if (exerciseCategory.getName().equals(newName)) {
            log.error("유효성 검사 실패: 새 이름이 현재 이름과 동일함");
            throw new ConflictException("새 카테고리 이름은 현재 이름과 같을 수 없습니다.", ErrorCode.CONFLICT_EXCEPTION);
        }

        boolean exists = exerciseCategoryRepository.existsByName(newName);
        if (exists) {
            log.error("유효성 검사 실패: 동일한 이름의 카테고리가 이미 존재함: {}", newName);
            throw new ConflictException("이미 존재하는 카테고리 이름입니다: " + newName, ErrorCode.CONFLICT_EXCEPTION);
        }

        log.info("카테고리 수정 유효성 검사 통과: 새 이름={}", newName);
    }

    // 카테고리 생성
    public ExerciseCategory createCategory(ExerciseCategoryRequest exerciseCategoryRequest) {
        log.info("카테고리 생성 요청: {}", exerciseCategoryRequest.getName());
        validateCategoryName(exerciseCategoryRequest.getName());

        ExerciseCategory exerciseCategory = new ExerciseCategory(exerciseCategoryRequest.getName());
        ExerciseCategory savedCategory = exerciseCategoryRepository.save(exerciseCategory);
        log.info("카테고리 생성 완료: {}", savedCategory.getId());
        return savedCategory;
    }

    // 카테고리 수정
    public ExerciseCategory updateCategory(Long id, ExerciseCategoryRequest exerciseCategoryRequest) {
        log.info("카테고리 수정 요청: ID={}, 새 이름={}", id, exerciseCategoryRequest.getName());

        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
                });

        // 유효성 검사 추가
        validateUpdateCategory(exerciseCategoryRequest.getName(), exerciseCategory);

        exerciseCategory.updateExerciseCategory(exerciseCategoryRequest.getName());
        ExerciseCategory updatedCategory = exerciseCategoryRepository.save(exerciseCategory);
        log.info("카테고리 수정 완료: ID={}", updatedCategory.getId());
        return updatedCategory;
    }

    // 카테고리 삭제
    public void deleteCategory(Long id) {
        log.info("카테고리 삭제 요청: ID={}", id);
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
                });

        exerciseCategoryRepository.delete(exerciseCategory);
        log.info("카테고리 삭제 완료: ID={}", id);
    }

    // 특정 카테고리 조회
    public ExerciseCategoryResponse getCategoryById(Long id) {
        log.info("특정 카테고리 조회 요청: ID={}", id);
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("카테고리 ID {} 찾을 수 없음", id);
                    return new NotFoundException("카테고리를 찾을 수 없습니다. ID: " + id, ErrorCode.NOT_FOUND_EXCEPTION);
                });

        ExerciseCategoryResponse response = ExerciseCategoryResponse.builder()
                .name(exerciseCategory.getName())
                .createdAt(exerciseCategory.getCreatedAt())
                .updatedAt(exerciseCategory.getUpdatedAt())
                .build();
        log.info("카테고리 조회 성공: ID={}", id);
        return response;
    }

    // 전체 카테고리 조회
    public List<ExerciseCategoryResponse> getAllCategories() {
        log.info("전체 카테고리 조회 요청");
        List<ExerciseCategoryResponse> categories = exerciseCategoryRepository.findAll()
                .stream()
                .map(exerciseCategory -> ExerciseCategoryResponse.builder()
                        .name(exerciseCategory.getName())
                        .createdAt(exerciseCategory.getCreatedAt())
                        .updatedAt(exerciseCategory.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
        log.info("전체 카테고리 조회 완료, 총 개수: {}", categories.size());
        return categories;
    }

}
