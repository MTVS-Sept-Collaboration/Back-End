package com.homefit.backend.Itemcategory.entity;


import com.homefit.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
