package com.homefit.backend.exerciselog.log;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exerciseLogs")
@Tag(name = "운동 log API(관리자전용)", description = "운동 로그 관리자전용")
public class ExerciseLogsController {
    private final ExerciseLogsService exerciseLogsService;

    public ExerciseLogsController(ExerciseLogsService exerciseLogsService) {
        this.exerciseLogsService = exerciseLogsService;
    }

    // 운동 선호도 조회
    @Operation(summary = "운동 선호도 조회", description = "특정 날짜의 운동별 선호도를 조회합니다.")
    @GetMapping("/popularity")
    public ResponseEntity<Map<String, Integer>> getExercisePopularity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(date);
        Map<String, Integer> exercisePopularity = exerciseLogsService.collectExercisePopularity(logEntries);
        return ResponseEntity.ok(exercisePopularity);
    }

    // 유저별 운동 횟수 조회
    @Operation(summary = "유저별 운동 횟수 조회", description = "특정 날짜의 유저별 운동 횟수를 조회합니다.")
    @GetMapping("/user/counts")
    public ResponseEntity<Map<Long, Integer>> getUserExerciseCounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(date);
        Map<Long, Integer> userExerciseCounts = exerciseLogsService.collectUserExerciseCounts(logEntries);
        return ResponseEntity.ok(userExerciseCounts);
    }

    // 전체 운동 횟수 조회
    @Operation(summary = "전체 운동 횟수 조회", description = "특정 날짜의 전체 운동 횟수를 조회합니다.")
    @GetMapping("/total/count")
    public ResponseEntity<Integer> getTotalExerciseCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(date);
        int totalExerciseCount = exerciseLogsService.collectTotalExerciseCount(logEntries);
        return ResponseEntity.ok(totalExerciseCount);
    }

    // 날짜별 운동 기록 조회
    @Operation(summary = "날짜별 운동 기록 조회", description = "특정 날짜의 운동 기록을 날짜별로 조회합니다.")
    @GetMapping("/by/date")
    public ResponseEntity<Map<LocalDate, List<String>>> getExerciseByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(date);
        Map<LocalDate, List<String>> exerciseByDate = exerciseLogsService.collectExerciseByDate(logEntries);
        return ResponseEntity.ok(exerciseByDate);
    }

    // 운동 카운트 조회
    @Operation(summary = "운동 카운트 조회", description = "특정 날짜의 유저별 운동 카운트를 조회합니다.")
    @GetMapping("/counts")
    public ResponseEntity<Map<Long, Map<String, Integer>>> getExerciseCounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(date);
        Map<Long, Map<String, Integer>> exerciseCounts = exerciseLogsService.collectExerciseCounts(logEntries);
        return ResponseEntity.ok(exerciseCounts);
    }
}
