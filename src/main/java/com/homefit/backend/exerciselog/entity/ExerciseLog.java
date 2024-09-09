package com.homefit.backend.exerciselog.entity;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.global.entity.BaseEntity;
import com.homefit.backend.login.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private Double caloriesBurned;
    private Integer exerciseCount;
    private Duration exerciseTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exercise_log_id") // Exercise 테이블에 외래 키를 생성
    private List<Exercise> exercises = new ArrayList<>();

    public ExerciseLog(LocalDateTime date, Double caloriesBurned, Integer exerciseCount, Duration exerciseTime, User user) {
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.exerciseCount = exerciseCount;
        this.exerciseTime = exerciseTime;
        this.user = user;
    }

    public void updateExerciseLog(LocalDateTime date, Double caloriesBurned, Integer exerciseCount, Duration exerciseTime) {
        if (date != null) {
            this.date = date;
        }
        if (caloriesBurned != null && caloriesBurned > 0) {
            this.caloriesBurned = caloriesBurned;
        }
        if (exerciseCount != null && exerciseCount > 0) {
            this.exerciseCount = exerciseCount;
        }
        if (exerciseTime != null && !exerciseTime.isNegative()) {
            this.exerciseTime = exerciseTime;
        }
    }
}
