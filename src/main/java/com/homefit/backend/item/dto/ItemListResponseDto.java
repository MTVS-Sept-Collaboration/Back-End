package com.homefit.backend.item.dto;

import com.homefit.backend.itemcategory.entity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemListResponseDto {

    private Long id;
    private ItemCategory itemCategory;

}
