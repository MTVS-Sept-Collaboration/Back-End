package com.homefit.backend.Item.dto;

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
