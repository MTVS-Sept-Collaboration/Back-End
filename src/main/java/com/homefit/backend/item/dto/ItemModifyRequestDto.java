package com.homefit.backend.item.dto;

import com.homefit.backend.category.item.entity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemModifyRequestDto {

    private ItemCategory itemCategory;
}
