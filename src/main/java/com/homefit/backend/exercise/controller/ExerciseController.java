package com.homefit.backend.exercise.controller;

import com.homefit.backend.exercise.dto.ExerciseRequest;
import com.homefit.backend.exercise.dto.ExerciseResponse;
import com.homefit.backend.exercise.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercises")
@Tag(name = "운동 API", description = "운동 관련 CRUD API")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Autowired
    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // 운동 생성
    @Operation(summary = "운동 생성", description = "새로운 운동을 생성합니다.")
    @PostMapping
    public ResponseEntity<ExerciseResponse> createExercise(
            @RequestBody @Valid ExerciseRequest exerciseRequest) {
        ExerciseResponse exerciseResponse = exerciseService.createExercise(exerciseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(exerciseResponse);
    }

    // 운동 수정
    @Operation(summary = "운동 수정", description = "기존 운동 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Long id,
            @RequestBody @Valid ExerciseRequest exerciseRequest) {
        ExerciseResponse exerciseResponse = exerciseService.updateExercise(id, exerciseRequest);
        return ResponseEntity.ok(exerciseResponse);
    }

    // 운동 삭제
    @Operation(summary = "운동 삭제", description = "특정 ID에 해당하는 운동을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    // 특정 운동 조회
    @Operation(summary = "특정 운동 조회", description = "특정 ID에 해당하는 운동 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable Long id) {
        ExerciseResponse exerciseResponse = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exerciseResponse);
    }

    // 전체 운동 조회 (카테고리 필터링 포함)
    @Operation(summary = "전체 운동 조회", description = "모든 운동 정보를 조회하거나 카테고리로 필터링합니다.")
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAllExercises(
            @RequestParam(value = "category", required = false) Optional<String> categoryName) {
        List<ExerciseResponse> exerciseResponses = exerciseService.getAllExercises(categoryName);
        return ResponseEntity.ok(exerciseResponses);
    }
}
