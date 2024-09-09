package com.homefit.backend.category.item.entity;


import com.homefit.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_category_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public ItemCategory(String name) {
        this.name = name;
    }

    public void updateCategory(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }
    }
}
