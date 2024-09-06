package com.homefit.backend.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "운동 생성 요청 DTO")
public class ExerciseRequest {

    @Schema(description = "운동명", example = "푸시업")
    @NotBlank(message = "운동 이름은 필수 입력 사항입니다.")
    private String exerciseName;

    @Schema(description = "운동 카테고리 ID", example = "1")
    @NotNull(message = "운동 카테고리는 필수 입력 사항입니다.")
    private Long exerciseCategoryId;

}
