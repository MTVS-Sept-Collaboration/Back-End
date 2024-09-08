package com.homefit.backend.itemcategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ItemCategoryResponse {

    @Schema(description = "Item 카테고리 이름", example = "헤어")
    private String name;

    @Schema(description = "Item 카테고리 생성 시간", example = "2024-09-06T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Item 카테고리 수정 시간", example = "2024-09-06T12:00:00")
    private LocalDateTime updatedAt;
}