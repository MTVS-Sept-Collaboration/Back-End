package com.homefit.backend.exerciselog.repository;

import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
    boolean existsByExercisesContaining(Exercise exercise);

    List<ExerciseLog> findByUser(User user);

    List<ExerciseLog> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
