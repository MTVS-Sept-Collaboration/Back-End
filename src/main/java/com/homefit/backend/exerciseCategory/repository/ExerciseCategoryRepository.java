package com.homefit.backend.exerciseCategory.repository;

import com.homefit.backend.exerciseCategory.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Long> {
    boolean existsByName(String name);
    Optional<ExerciseCategory> findByName(String name);
}
