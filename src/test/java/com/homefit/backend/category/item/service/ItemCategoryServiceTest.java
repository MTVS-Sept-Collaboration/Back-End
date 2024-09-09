package com.homefit.backend.category.item.service;

import com.homefit.backend.category.item.dto.ItemCategoryRequest;
import com.homefit.backend.category.item.dto.ItemCategoryResponse;
import com.homefit.backend.category.item.entity.ItemCategory;
import com.homefit.backend.category.item.repository.ItemCategoryRepository;
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
public class ItemCategoryServiceTest {

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    @Autowired
    private ItemCategoryService itemCategoryService;

    // 카테고리 생성
    @Test
    @DisplayName("아이템 카테고리 생성")
    public void testCreateCategory() {
        //Given
        ItemCategoryRequest itemCategoryRequest = ItemCategoryRequest.builder()
                .name("헤어")
                .build();

        // When
        ItemCategory createdItemCategory = itemCategoryService.createCategory(itemCategoryRequest);

        // Then
        Assertions.assertThat(createdItemCategory.getId()).isNotNull();
        Assertions.assertThat(createdItemCategory.getName()).isEqualTo("헤어");

        // 추가로 데이터베이스에서 카테고리가 잘 저장되었는지 확인
        ItemCategory foundItemCategory = itemCategoryRepository.findById(createdItemCategory.getId()).orElse(null);
        Assertions.assertThat(foundItemCategory).isNotNull();
        Assertions.assertThat(foundItemCategory.getName()).isEqualTo("헤어");

        // 생성 시간과 수정 시간 검증
        Assertions.assertThat(foundItemCategory.getCreatedAt()).isNotNull();
        Assertions.assertThat(foundItemCategory.getUpdatedAt()).isNotNull();
    }

    // 카테고리 중복 이름 생성 시도 테스트
    @Test
    @DisplayName("아이템 카테고리 중복 이름 생성")
    public void testCreateCategoryWithDuplicateName() {
        // Given
        itemCategoryService.createCategory(ItemCategoryRequest.builder()
                .name("상의")
                .build());

        ItemCategoryRequest duplicateRequest = ItemCategoryRequest.builder()
                .name("상의")
                .build();

        // When & Then
        Assertions.assertThatThrownBy(() -> itemCategoryService.createCategory(duplicateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("이미 존재하는 카테고리 이름입니다");
    }

    // 카테고리 수정 테스트
    @Test
    @DisplayName("아이템 카테고리 수정")
    public void testUpdateCategory() {
        //Given
        ItemCategory savedItemCategory = itemCategoryRepository.save(new ItemCategory("헤어"));
        ItemCategoryRequest updateRequest = ItemCategoryRequest.builder()
                .name("상의")
                .build();

        // When
        ItemCategory updatedItemCategory = itemCategoryService.updateCategory(savedItemCategory.getId(), updateRequest);

        // Then
        Assertions.assertThat(updatedItemCategory.getName()).isEqualTo("상의");

        // 데이터베이스에서 수정된 데이터 확인
        ItemCategory foundItemCategory = itemCategoryRepository.findById(savedItemCategory.getId()).orElse(null);
        Assertions.assertThat(foundItemCategory).isNotNull();
        Assertions.assertThat(foundItemCategory.getName()).isEqualTo("상의");

        // 수정 시간 검증 (수정 시간은 생성 시간과 같거나 이후여야 함)
        Assertions.assertThat(foundItemCategory.getUpdatedAt()).isAfterOrEqualTo(foundItemCategory.getCreatedAt());
    }

    // 수정 시 동일한 이름으로 수정하는 경우 테스트
    @Test
    @DisplayName("아이템 동일한 이름으로 수정")
    public void testUpdateCategoryWithSameName() {
        // Given
        ItemCategory savedItemCategory = itemCategoryRepository.save(new ItemCategory("상의"));
        ItemCategoryRequest updateRequest = ItemCategoryRequest.builder()
                .name("상의")
                .build();

        // When & Then
        Assertions.assertThatThrownBy(() -> itemCategoryService.updateCategory(savedItemCategory.getId(), updateRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("새 카테고리 이름은 현재 이름과 같을 수 없습니다");
    }


    // 카테고리 삭제 테스트
    @Test
    @DisplayName("아이템 카테고리 삭제")
    public void testDeleteCategory() {
        // Given
        ItemCategory savedItemCategory = itemCategoryRepository.save(new ItemCategory("하의"));

        // When
        itemCategoryService.deleteCategory(savedItemCategory.getId());

        // Then
        ItemCategory foundItemCategory = itemCategoryRepository.findById(savedItemCategory.getId()).orElse(null);
        Assertions.assertThat(foundItemCategory).isNull();
    }


    // 특정 카테고리 조회 테스트
    @Test
    @DisplayName("특정 아이템 카테고리 조회")
    public void testGetCategoryById() {
        // Given
        ItemCategory savedItemCategory = itemCategoryRepository.save(new ItemCategory("상의"));

        // When
        ItemCategoryResponse foundCategory = itemCategoryService.getItemCategoryById(savedItemCategory.getId());

        // Then
        Assertions.assertThat(foundCategory).isNotNull();
        Assertions.assertThat(foundCategory.getName()).isEqualTo("상의");

        // 생성 시간과 수정 시간 검증
        Assertions.assertThat(foundCategory.getCreatedAt()).isNotNull();
        Assertions.assertThat(foundCategory.getUpdatedAt()).isNotNull();
    }

    // 전체 카테고리 조회 테스트
    @Test
    @DisplayName("아이템 전체 카테고리 조회")
    public void testGetAllCategories() {
        // Given
        itemCategoryRepository.save(new ItemCategory("상의"));
        itemCategoryRepository.save(new ItemCategory("하의"));

        // When
        List<ItemCategoryResponse> categories = itemCategoryService.getAllItemCategories();

        // Then
        Assertions.assertThat(categories.size()).isEqualTo(2); // 카테고리 리스트 크기 검증
        Assertions.assertThat(categories)
                .extracting(ItemCategoryResponse::getName) // "name" 필드를 추출
                .containsExactlyInAnyOrder("상의", "하의"); // "상의", "하의"가 포함되어 있어야 함

        // 각 카테고리의 생성 시간과 수정 시간 검증
        for (ItemCategoryResponse category : categories) {
            Assertions.assertThat(category.getCreatedAt()).isNotNull();
            Assertions.assertThat(category.getUpdatedAt()).isNotNull();
        }
    }

}