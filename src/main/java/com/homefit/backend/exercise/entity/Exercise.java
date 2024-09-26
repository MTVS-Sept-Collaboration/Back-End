package com.homefit.backend.exercise.entity;

import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(mappedBy = "exercise")  // 운동 기록과 1:N 관계 설정
    private List<ExerciseLog> exerciseLogs;

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
