package com.homefit.backend.category.exercise.entity;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "exerciseCategory", cascade = CascadeType.ALL)
    private List<Exercise> exercises = new ArrayList<>();

    public ExerciseCategory(String name) {
        this.name = name;
    }

    public void updateExerciseCategory(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }
    }
}
