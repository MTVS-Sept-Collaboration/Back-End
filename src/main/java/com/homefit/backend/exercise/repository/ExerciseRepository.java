package com.homefit.backend.exercise.repository;


import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exerciseCategory.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository  extends JpaRepository<Exercise, Long> {
    boolean existsByExerciseName(String exerciseName);
    List<Exercise> findByExerciseCategory(ExerciseCategory exerciseCategory);
}
