package com.homefit.backend.exerciselog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 기록 생성 요청 DTO")
public class ExerciseLogRequest {

    @Schema(description = "운동 기록 날짜", example = "2024-09-06")
    private LocalDate date;

    @Schema(description = "소모된 칼로리", example = "500.5")
    private Double caloriesBurned;

    @Schema(description = "운동 횟수", example = "10")
    private Integer exerciseCount;

    @Schema(description = "운동 시작 시간", example = "101530")
    private String startTime;

    @Schema(description = "운동 끝 시간", example = "114530")
    private String endTime;

    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "운동 ID", example = "1")
    private Long exerciseId;
}

