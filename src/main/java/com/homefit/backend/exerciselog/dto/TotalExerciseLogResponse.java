package com.homefit.backend.exerciselog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "총합 운동 기록 응답 DTO")
public class TotalExerciseLogResponse {

    @Schema(description = "운동 기록 날짜", example = "2024-09-06")
    private LocalDate date;

    @Schema(description = "총 소모된 칼로리", example = "1000.5")
    private Double totalCaloriesBurned;

    @Schema(description = "총 운동 횟수", example = "50")
    private Integer totalExerciseCount;
}
