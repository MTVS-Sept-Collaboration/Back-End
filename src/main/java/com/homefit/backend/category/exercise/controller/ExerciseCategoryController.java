package com.homefit.backend.category.exercise.controller;

import com.homefit.backend.category.exercise.dto.ExerciseCategoryRequest;
import com.homefit.backend.category.exercise.dto.ExerciseCategoryResponse;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.service.ExerciseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ExerciseCategory")
@Tag(name = "운동 카테고리", description = "운동 카테고리 API")
public class ExerciseCategoryController {

    private final ExerciseCategoryService exerciseCategoryService;

    @Autowired
    public ExerciseCategoryController(ExerciseCategoryService exerciseCategoryService) {
        this.exerciseCategoryService = exerciseCategoryService;
    }

    @Operation(summary = "운동 카테고리 생성", description = "새로운 운동 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<ExerciseCategory> createCategory(@RequestBody ExerciseCategoryRequest exerciseCategoryRequest) {
        ExerciseCategory createdCategory = exerciseCategoryService.createCategory(exerciseCategoryRequest);
        return ResponseEntity.ok(createdCategory);
    }

    @Operation(summary = "운동 카테고리 수정", description = "기존 운동 카테고리의 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseCategory> updateCategory(@PathVariable Long id, @RequestBody ExerciseCategoryRequest exerciseCategoryRequest) {
        ExerciseCategory updatedCategory = exerciseCategoryService.updateCategory(id, exerciseCategoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "운동 카테고리 삭제", description = "특정 ID에 해당하는 운동 카테고리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        exerciseCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "특정 운동 카테고리 조회", description = "특정 ID에 해당하는 운동 카테고리의 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseCategoryResponse> getCategoryById(@PathVariable Long id) {
        ExerciseCategoryResponse categoryResponse = exerciseCategoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponse);
    }

    @Operation(summary = "전체 운동 카테고리 조회", description = "모든 운동 카테고리의 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ExerciseCategoryResponse>> getAllCategories() {
        List<ExerciseCategoryResponse> categories = exerciseCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
