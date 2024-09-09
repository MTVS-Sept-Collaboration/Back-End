package com.homefit.backend.category.item.controller;

import com.homefit.backend.category.item.dto.ItemCategoryRequest;
import com.homefit.backend.category.item.dto.ItemCategoryResponse;
import com.homefit.backend.category.item.entity.ItemCategory;
import com.homefit.backend.category.item.service.ItemCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itemCategory")
@Tag(name = "Item 카테고리", description = "Item 카테고리 관련 API")
public class ItemCategoryController {

    private final ItemCategoryService itemCategoryService;

    @Autowired
    public ItemCategoryController(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    // 카테고리 생성
    @Operation(summary = "Item 카테고리 생성", description = "새로운 Item 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<ItemCategory> createCategory(@RequestBody ItemCategoryRequest itemCategoryRequest) {
        ItemCategory createdItemCategory = itemCategoryService.createCategory(itemCategoryRequest);
        return ResponseEntity.ok(createdItemCategory);
    }

    // 카테고리 수정
    @Operation(summary = "Item 카테고리 수정", description = "기존 Item 카테고리를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ItemCategory> updateCategory(@PathVariable Long id, @RequestBody ItemCategoryRequest itemCategoryRequest) {
        ItemCategory updatedItemCategory = itemCategoryService.updateCategory(id, itemCategoryRequest);
        return ResponseEntity.ok(updatedItemCategory);
    }

    // 카테고리 삭제
    @Operation(summary = "Item 카테고리 삭제", description = "특정 Item 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        itemCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 특정 카테고리 조회
    @Operation(summary = "특정 Item 카테고리 조회", description = "ID를 기반으로 특정 Item 카테고리를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ItemCategoryResponse> getItemCategoryById(@PathVariable Long id) {
        ItemCategoryResponse itemCategoryResponse = itemCategoryService.getItemCategoryById(id);
        return ResponseEntity.ok(itemCategoryResponse);
    }

    // 전체 카테고리 조회
    @Operation(summary = "전체 Item 카테고리 조회", description = "모든 Item 카테고리를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ItemCategoryResponse>> getAllItemCategories() {
        List<ItemCategoryResponse> categories = itemCategoryService.getAllItemCategories();
        return ResponseEntity.ok(categories);
    }
}
