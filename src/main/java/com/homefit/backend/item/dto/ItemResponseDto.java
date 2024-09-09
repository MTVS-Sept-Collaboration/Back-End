package com.homefit.backend.item.dto;

import com.homefit.backend.category.item.entity.ItemCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemResponseDto {

    private Long id;
    private ItemCategory category;
}
