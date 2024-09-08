package com.homefit.backend.item.dto;

import com.homefit.backend.item.entity.Item;
import com.homefit.backend.itemcategory.entity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSaveRequestDto {

    private ItemCategory itemCategory;

    public Item toEntity() {
        return Item.builder()
                .itemCategory(itemCategory)
                .build();
    }
}
