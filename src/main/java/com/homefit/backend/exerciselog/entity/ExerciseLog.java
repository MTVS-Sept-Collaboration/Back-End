package com.homefit.backend.exerciselog.entity;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.global.entity.BaseEntity;
import com.homefit.backend.login.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ExerciseLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double caloriesBurned;
    private Integer exerciseCount;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "exercise_id")  // 운동과 1:1 관계 설정
    private Exercise exercise;

    public ExerciseLog(LocalDate date, Double caloriesBurned, Integer exerciseCount, LocalTime startTime, LocalTime endTime, User user, Exercise exercise) {
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.exerciseCount = exerciseCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.user = user;
        this.exercise = exercise;
    }

    // 기존 update 메서드 수정
    public void updateExerciseLog(LocalDate date, Double caloriesBurned, Integer exerciseCount, LocalTime startTime, LocalTime endTime) {
        if (date != null) {
            this.date = date;
        }
        if (caloriesBurned != null && caloriesBurned > 0) {
            this.caloriesBurned = caloriesBurned;
        }
        if (exerciseCount != null && exerciseCount > 0) {
            this.exerciseCount = exerciseCount;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
    }

    // setExercise 메서드 추가
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
