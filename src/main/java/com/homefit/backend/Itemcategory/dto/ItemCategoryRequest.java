package com.homefit.backend.Itemcategory.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "Item 카테고리 생성 요청 DTO")
public class ItemCategoryRequest {

    @Schema(description = "Item 카테고리 이름", example = "헤어")
    private String name;
}
