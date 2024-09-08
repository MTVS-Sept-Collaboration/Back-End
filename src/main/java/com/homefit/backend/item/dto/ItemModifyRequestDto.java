package com.homefit.backend.item.dto;

import com.homefit.backend.itemcategory.entity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemModifyRequestDto {

    private ItemCategory itemCategory;
}
