package com.homefit.backend.exerciselog.repository;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    boolean existsByExercise(Exercise exercise);

    List<ExerciseLog> findByUser(User user);

    List<ExerciseLog> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
