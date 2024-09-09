package com.homefit.backend.category.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 카테고리 생성 요청 DTO")
public class ExerciseCategoryRequest {

    @Schema(description = "운동 카테고리 이름", example = "맨몸운동")
    private String name;
}
