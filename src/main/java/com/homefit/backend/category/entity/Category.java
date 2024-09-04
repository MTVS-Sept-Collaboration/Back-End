package com.homefit.backend.category.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public void updateCategory(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }
    }
}
