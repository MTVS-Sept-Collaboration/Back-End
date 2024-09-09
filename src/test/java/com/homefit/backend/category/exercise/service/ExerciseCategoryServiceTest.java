package com.homefit.backend.category.exercise.service;

import com.homefit.backend.category.exercise.dto.ExerciseCategoryRequest;
import com.homefit.backend.category.exercise.dto.ExerciseCategoryResponse;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.repository.ExerciseCategoryRepository;
import com.homefit.backend.global.exception.model.ConflictException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class ExerciseCategoryServiceTest {

    @Autowired
    private ExerciseCategoryService exerciseCategoryService;

    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;

    // 카테고리 생성 테스트
    @Test
    @DisplayName("운동 카테고리 생성")
    public void testCreateCategory() {
        // Given
        ExerciseCategoryRequest request = ExerciseCategoryRequest.builder()
                .name("필라테스")
                .build();

        // When
        ExerciseCategory createdCategory = exerciseCategoryService.createCategory(request);

        // Then*
        Assertions.assertThat(createdCategory.getId()).isNotNull();
        Assertions.assertThat(createdCategory.getName()).isEqualTo("필라테스");

        // 추가로 데이터베이스에서 카테고리가 잘 저장되었는지 확인
        ExerciseCategory foundCategory = exerciseCategoryRepository.findById(createdCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("필라테스");

        // 생성 시간과 수정 시간 검증
        Assertions.assertThat(foundCategory.getCreatedAt()).isNotNull();
        Assertions.assertThat(foundCategory.getUpdatedAt()).isNotNull();
    }

    // 카테고리 중복 이름 생성 시도 테스트
    @Test
    @DisplayName("운동 카테고리 중복 이름 생성")
    public void testCreateCategoryWithDuplicateName() {
        // Given
        exerciseCategoryService.createCategory(ExerciseCategoryRequest.builder()
                .name("요가")
                .build());

        ExerciseCategoryRequest duplicateRequest = ExerciseCategoryRequest.builder()
                .name("요가")
                .build();

        // When & Then
        Assertions.assertThatThrownBy(() -> exerciseCategoryService.createCategory(duplicateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다");
    }

    // 카테고리 수정 테스트
    @Test
    @DisplayName("운동 카테고리 수정")
    public void testUpdateCategory() {
        // Given
        ExerciseCategory savedCategory = exerciseCategoryRepository.save(new ExerciseCategory("요가"));
        ExerciseCategoryRequest updateRequest = ExerciseCategoryRequest.builder()
                .name("필라테스")
                .build();

        // When
        ExerciseCategory updatedCategory = exerciseCategoryService.updateCategory(savedCategory.getId(), updateRequest);

        // Then
        Assertions.assertThat(updatedCategory.getName()).isEqualTo("필라테스");

        // 데이터베이스에서 수정된 카테고리 조회 및 검증
        ExerciseCategory foundCategory = exerciseCategoryRepository.findById(savedCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("필라테스");

        // 생성 시간과 수정 시간 검증
        Assertions.assertThat(foundCategory.getCreatedAt()).isNotNull();
        Assertions.assertThat(foundCategory.getUpdatedAt()).isNotNull();
        Assertions.assertThat(foundCategory.getUpdatedAt()).isAfterOrEqualTo(foundCategory.getCreatedAt());
    }

    // 수정 시 동일한 이름으로 수정하는 경우 테스트
    @Test
    @DisplayName("운동 카테고리 동일하는 이름으로 수정")
    public void testUpdateCategoryWithSameName() {
        // Given*
        ExerciseCategory savedCategory = exerciseCategoryRepository.save(new ExerciseCategory("요가"));
        ExerciseCategoryRequest updateRequest = ExerciseCategoryRequest.builder()
                .name("요가")
                .build();

        // When & Then
        Assertions.assertThatThrownBy(() -> exerciseCategoryService.updateCategory(savedCategory.getId(), updateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("새 카테고리 이름은 현재 이름과 같을 수 없습니다");
    }

    // 카테고리 삭제 테스트
    @Test
    @DisplayName("카테고리 삭제")
    public void testDeleteCategory() {
        // Given*
        ExerciseCategory savedCategory = exerciseCategoryRepository.save(new ExerciseCategory("요가"));

        // When*
        exerciseCategoryService.deleteCategory(savedCategory.getId());

        // Then*
        Assertions.assertThat(exerciseCategoryRepository.existsById(savedCategory.getId())).isFalse();
    }

    // 특정 카테고리 조회 테스트
    @Test
    @DisplayName("운동 특정 카테고리 조회")
    public void testGetCategoryById() {
        // Given
        ExerciseCategory savedCategory = exerciseCategoryRepository.save(new ExerciseCategory("필라테스"));

        // When
        ExerciseCategoryResponse foundCategory = exerciseCategoryService.getCategoryById(savedCategory.getId());

        // Then*
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("필라테스");

        // 데이터베이스에서 조회된 카테고리와 비교
        ExerciseCategory foundCategoryDb = exerciseCategoryRepository.findById(savedCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategoryDb).isNotNull();
        Assertions.assertThat(foundCategoryDb.getName()).isEqualTo("필라테스");

        // 생성 시간과 수정 시간 검증
        Assertions.assertThat(foundCategoryDb.getCreatedAt()).isNotNull();
        Assertions.assertThat(foundCategoryDb.getUpdatedAt()).isNotNull();
    }

    // 전체 카테고리 조회 테스트
    @Test
    @DisplayName("전체 운동 카테고리 조회")
    public void testGetAllCategories() {
        // Given
        exerciseCategoryRepository.save(new ExerciseCategory("요가"));
        exerciseCategoryRepository.save(new ExerciseCategory("필라테스"));

        // When*
        List<ExerciseCategoryResponse> categories = exerciseCategoryService.getAllCategories();

        // Then
        Assertions.assertThat(categories).hasSize(2);
        Assertions.assertThat(categories).extracting(ExerciseCategoryResponse::getName)
                .containsExactlyInAnyOrder("요가", "필라테스");

        //* *데이터베이스에서 조회된 카테고리 개수 확인*
        List<ExerciseCategory> foundCategories = exerciseCategoryRepository.findAll();
        Assertions.assertThat(foundCategories).hasSize(2);
        Assertions.assertThat(foundCategories).extracting(ExerciseCategory::getName)
                .containsExactlyInAnyOrder("요가", "필라테스");
    }

}

