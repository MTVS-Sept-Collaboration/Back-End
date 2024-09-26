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
import java.util.Optional;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {

    boolean existsByExercise(Exercise exercise);

    // 운동 ID로 운동 기록의 개수를 확인하는 메서드
    int countByExerciseId(Long exerciseId);

    Optional<ExerciseLog> findByUserAndDateAndExercise(User user, LocalDate date, Exercise exercise);

    List<ExerciseLog> findByUser(User user);

    List<ExerciseLog> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // 이 메서드를 통해 날짜와 유저로 운동 로그 목록을 조회
    List<ExerciseLog> findByUserAndDate(User user, LocalDate date);
}
