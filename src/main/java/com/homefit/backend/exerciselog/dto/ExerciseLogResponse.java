package com.homefit.backend.exerciselog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 기록 응답 DTO")
public class ExerciseLogResponse {

    @Schema(description = "운동 기록 ID", example = "1")
    private Long id;

    @Schema(description = "운동 기록 날짜", example = "2024-09-06")
    private LocalDate date;

    @Schema(description = "소모된 칼로리", example = "500.5")
    private Double caloriesBurned;

    @Schema(description = "운동 횟수", example = "10")
    private Integer exerciseCount;

    @Schema(description = "운동 ID", example = "1")
    private Long exerciseId;
}


