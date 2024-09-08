package com.homefit.backend.item.entity;

import com.homefit.backend.item.dto.ItemModifyRequestDto;
import com.homefit.backend.itemcategory.entity.ItemCategory;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_category_id") // FK
    private ItemCategory itemCategory;

    @Builder
    public Item(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public void update(ItemModifyRequestDto requestDto) {
        this.itemCategory = requestDto.getItemCategory();
    }
}
