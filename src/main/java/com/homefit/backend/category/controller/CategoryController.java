package com.homefit.backend.category.controller;

import com.homefit.backend.category.dto.CategoryRequest;
import com.homefit.backend.category.dto.CategoryResponse;
import com.homefit.backend.category.entity.Category;
import com.homefit.backend.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 카테고리 생성
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest categoryRequest) {
        Category createdCategory = categoryService.createCategory(categoryRequest);
        return ResponseEntity.ok(createdCategory);
    }

    // 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest categoryRequest) {
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    // 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 특정 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponse);
    }

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
