package com.homefit.backend.category.dto;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryResponse {
    private String name;
}