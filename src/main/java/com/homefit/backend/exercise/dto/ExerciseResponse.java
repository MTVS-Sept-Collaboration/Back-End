package com.homefit.backend.exercise.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 응답 DTO")
public class ExerciseResponse {

    @Schema(description = "운동 ID", example = "1")
    private Long id;

    @Schema(description = "운동명", example = "푸시업")
    private String exerciseName;

    @Schema(description = "운동 카테고리명", example = "근력운동")
    private String exerciseCategoryName;

    @Schema(description = "생성 시간", example = "2024-09-06T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2024-09-07T12:20:45")
    private LocalDateTime updatedAt;
}
