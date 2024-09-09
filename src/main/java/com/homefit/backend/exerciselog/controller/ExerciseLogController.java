package com.homefit.backend.exerciselog.controller;

import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.service.ExerciseLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exerciseLogs")
@Tag(name = "운동 기록 API", description = "운동 기록 API")
public class ExerciseLogController {

    private final ExerciseLogService exerciseLogService;

    @Autowired
    public ExerciseLogController(ExerciseLogService exerciseLogService) {
        this.exerciseLogService = exerciseLogService;
    }

    @Operation(summary = "운동 기록 생성", description = "새로운 운동 기록을 생성합니다.")
    @PostMapping
    public ResponseEntity<ExerciseLogResponse> createExerciseLog(@RequestBody ExerciseLogRequest request) {
        ExerciseLogResponse response = exerciseLogService.createExerciseLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "운동 기록 수정", description = "기존의 운동 기록을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseLogResponse> updateExerciseLog(@PathVariable Long id, @RequestParam Long userId, @RequestBody ExerciseLogRequest request) {
        ExerciseLogResponse response = exerciseLogService.updateExerciseLog(id, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "운동 기록 삭제", description = "특정 ID의 운동 기록을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseLog(@PathVariable Long id, @RequestParam Long userId) {
        exerciseLogService.deleteExerciseLog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 유저의 운동 기록 조회", description = "특정 유저의 모든 운동 기록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExerciseLogResponse>> getExerciseLogsByUser(@PathVariable Long userId) {
        List<ExerciseLogResponse> responses = exerciseLogService.getExerciseLogsByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 유저의 특정 날짜 운동 기록 조회", description = "특정 유저의 특정 날짜 운동 기록을 조회합니다.")
    @GetMapping("/user/{userId}/date")
    public ResponseEntity<List<ExerciseLogResponse>> getExerciseLogsByUserAndDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogResponse> responses = exerciseLogService.getExerciseLogsByUserAndDate(userId, date);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 운동 기록 조회", description = "특정 ID의 운동 기록을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseLogResponse> getExerciseLogById(@PathVariable Long id) {
        ExerciseLogResponse response = exerciseLogService.getExerciseLogById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 운동 기록 조회", description = "모든 운동 기록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ExerciseLogResponse>> getAllExerciseLogs() {
        List<ExerciseLogResponse> responses = exerciseLogService.getAllExerciseLogs();
        return ResponseEntity.ok(responses);
    }
}
