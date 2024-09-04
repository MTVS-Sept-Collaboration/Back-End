package com.homefit.backend.Category.service;

import com.homefit.backend.category.dto.CategoryRequest;
import com.homefit.backend.category.dto.CategoryResponse;
import com.homefit.backend.category.entity.Category;
import com.homefit.backend.category.repository.CategoryRepository;
import com.homefit.backend.category.service.CategoryService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    // 카테고리 생성
    @Test
    @DisplayName("카테고리 생성")
    public void testCreateCategory() {

        //Given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .name("헤어")
                .build();

        // When
        Category createdCategory = categoryService.createCategory(categoryRequest);

        // Then
        Assertions.assertThat(createdCategory.getId()).isNotNull();
        Assertions.assertThat(createdCategory.getName()).isEqualTo("헤어");

        // 추가로 데이터베이스에서 카테고리가 잘 저장되었는지 확인
        Category foundCategory = categoryRepository.findById(createdCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("헤어");

    }

    // 카테고리 수정 테스트
    @Test
    @DisplayName("카테고리 수정")
    public void testUpdateCategory() {
        //Given
        Category savedCategory = categoryRepository.save(new Category("헤어"));
        CategoryRequest updateRequest = CategoryRequest.builder()
                .name("상의")
                .build();

        // When
        Category updatedCategory = categoryService.updateCategory(savedCategory.getId(), updateRequest);

        // Then
        Assertions.assertThat(updatedCategory.getName()).isEqualTo("상의");

        // 데이터베이스에서 수정된 데이터 확인
        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("상의");
    }

    @Test
    @DisplayName("카테고리 삭제")
    public void testDeleteCategory() {
        // Given
        Category savedCategory = categoryRepository.save(new Category("하의"));

        // When
        categoryService.deleteCategory(savedCategory.getId());

        // Then
        Category foundCategory = categoryRepository.findById(savedCategory.getId()).orElse(null);
        Assertions.assertThat(foundCategory).isNull();
    }

    // 카테고리 조회 테스트
    @Test
    @DisplayName("특정 카테고리 조회")
    public void testGetCategoryById() {
        // Given
        Category savedCategory = categoryRepository.save(new Category("상의"));

        // When
        CategoryResponse foundCategory = categoryService.getCategoryById(savedCategory.getId());

        // Then
        Assertions.assertThat(foundCategory).isNotNull(); // 조회된 카테고리가 null이 아니어야 함
        Assertions.assertThat(foundCategory.getName()).isEqualTo("상의"); // 이름이 "상의"여야 함
    }

    // 전체 카테고리 조회 테스트
    @Test
    @DisplayName("전체 카테고리 조회")
    public void testGetAllCategories() {
        // Given
        categoryRepository.save(new Category("상의"));
        categoryRepository.save(new Category("하의"));

        // When
        List<CategoryResponse> categories = categoryService.getAllCategories(); // DTO로 변환된 리스트

        // Then
        Assertions.assertThat(categories.size()).isEqualTo(2); // 카테고리 리스트 크기 검증
        Assertions.assertThat(categories)
                .extracting(CategoryResponse::getName) // "name" 필드를 추출
                .containsExactlyInAnyOrder("상의", "하의"); // "상의", "하의"가 포함되어 있어야 함
    }

}
