package com.homefit.backend.exerciseLog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 기록 응답 DTO")
public class ExerciseLogResponse {

    @Schema(description = "운동 기록 ID", example = "1")
    private Long id;

    @Schema(description = "운동 기록 날짜", example = "2024-09-06T10:15:30")
    private LocalDateTime date;

    @Schema(description = "소모된 칼로리", example = "500.5")
    private Double caloriesBurned;

    @Schema(description = "운동 횟수", example = "10")
    private Integer exerciseCount;

    @Schema(description = "운동 시간", example = "PT1H30M")  // ISO-8601 Duration 형식
    private Duration exerciseTime;

    @Schema(description = "운동 이름 리스트")
    private List<String> exerciseNames;

    @Schema(description = "생성 시간", example = "2024-09-06T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2024-09-07T12:20:45")
    private LocalDateTime updatedAt;
}
