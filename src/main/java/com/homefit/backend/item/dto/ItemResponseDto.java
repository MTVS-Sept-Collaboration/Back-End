package com.homefit.backend.item.dto;

import com.homefit.backend.itemcategory.entity.ItemCategory;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter
public class ItemResponseDto {

    private Long id;
    private ItemCategory category;
}
