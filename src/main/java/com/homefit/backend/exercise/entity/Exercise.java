package com.homefit.backend.exercise.entity;

import com.homefit.backend.exerciseCategory.entity.ExerciseCategory;
import com.homefit.backend.exerciseLog.entity.ExerciseLog;
import com.homefit.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercise_name")
    private String exerciseName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ExerciseCategory exerciseCategory;

    public Exercise(String exerciseName, ExerciseCategory exerciseCategory) {
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
    }

    public void updateExercise(String newExerciseName, ExerciseCategory newCategory) {
        if (newExerciseName != null && !newExerciseName.isEmpty()) {
            this.exerciseName = newExerciseName;
        }
        if (newCategory != null) {
            this.exerciseCategory = newCategory;
        }
    }

}
