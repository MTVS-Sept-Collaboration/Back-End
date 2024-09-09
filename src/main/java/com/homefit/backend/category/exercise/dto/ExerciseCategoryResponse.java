package com.homefit.backend.category.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 카테고리 응답 DTO")
public class ExerciseCategoryResponse {

    @Schema(description = "운동 카테고리 이름", example = "스쿼트")
    private String name;

    @Schema(description = "운동 카테고리 생성 시간", example = "2024-09-06T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "운동 카테고리 수정 시간", example = "2024-09-06T12:00:00")
    private LocalDateTime updatedAt;
}
