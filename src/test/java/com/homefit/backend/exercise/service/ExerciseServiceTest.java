package com.homefit.backend.exercise.service;

import com.homefit.backend.exercise.dto.ExerciseRequest;
import com.homefit.backend.exercise.dto.ExerciseResponse;
import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.repository.ExerciseCategoryRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.entity.ExerciseLog;
import com.homefit.backend.exerciselog.service.ExerciseLogService;
import com.homefit.backend.global.exception.model.ValidationException;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class ExerciseServiceTest {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseRepository exerciseRepository;


    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;

    @Autowired
    private ExerciseLogService exerciseLogService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("운동 생성 테스트")
    public void testCreateExercise() {
        // Given
        ExerciseCategory category = new ExerciseCategory("근력운동");
        exerciseCategoryRepository.save(category);

        ExerciseRequest request = ExerciseRequest.builder()
                .exerciseName("푸시업")
                .exerciseCategoryId(category.getId())
                .build();

        // When
        ExerciseResponse response = exerciseService.createExercise(request);

        // Then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getExerciseName()).isEqualTo("푸시업");
        Assertions.assertThat(response.getExerciseCategoryName()).isEqualTo("근력운동");

        // 추가로 데이터베이스에서 운동이 잘 저장되었는지 확인
        Exercise savedExercise = exerciseRepository.findById(response.getId()).orElse(null);
        Assertions.assertThat(savedExercise).isNotNull();
        Assertions.assertThat(savedExercise.getExerciseName()).isEqualTo("푸시업");
    }

    @Test
    @DisplayName("운동 수정 테스트")
    public void testUpdateExercise() {
        // Given
        ExerciseCategory category = new ExerciseCategory("근력운동");
        exerciseCategoryRepository.save(category);

        Exercise exercise = new Exercise("푸시업", category);
        exerciseRepository.save(exercise);

        ExerciseCategory newCategory = new ExerciseCategory("유산소운동");
        exerciseCategoryRepository.save(newCategory);

        ExerciseRequest updateRequest = ExerciseRequest.builder()
                .exerciseName("런지")
                .exerciseCategoryId(newCategory.getId())
                .build();

        // When
        ExerciseResponse updatedResponse = exerciseService.updateExercise(exercise.getId(), updateRequest);

        // Then
        Assertions.assertThat(updatedResponse.getExerciseName()).isEqualTo("런지");
        Assertions.assertThat(updatedResponse.getExerciseCategoryName()).isEqualTo("유산소운동");

        // 데이터베이스에서 업데이트된 운동 확인
        Exercise updatedExercise = exerciseRepository.findById(exercise.getId()).orElse(null);
        Assertions.assertThat(updatedExercise).isNotNull();
        Assertions.assertThat(updatedExercise.getExerciseName()).isEqualTo("런지");
        Assertions.assertThat(updatedExercise.getExerciseCategory().getName()).isEqualTo("유산소운동");
    }

    @Test
    @DisplayName("운동 삭제 테스트")
    public void testDeleteExercise() {
        // Given
        ExerciseCategory category = new ExerciseCategory("근력운동");
        exerciseCategoryRepository.save(category);

        Exercise exercise = new Exercise("푸시업", category);
        exerciseRepository.save(exercise);

        // When
        exerciseService.deleteExercise(exercise.getId());

        // Then
        Exercise deletedExercise = exerciseRepository.findById(exercise.getId()).orElse(null);
        Assertions.assertThat(deletedExercise).isNull();  // 운동이 삭제되었는지 확인
    }

    @Test
    @DisplayName("운동 삭제 시 운동 로그가 있는 경우 예외 발생 테스트")
    public void testDeleteExerciseWithLogs() {
        // Given
        ExerciseCategory category = new ExerciseCategory("근력운동");
        exerciseCategoryRepository.save(category);

        Exercise exercise = new Exercise("푸시업", category);
        exerciseRepository.save(exercise);

        // 유저 생성 및 운동 로그 생성
        User user = User.builder()
                .userName("testUser")
                .password("password")
                .role(RoleType.USER)
                .build();

        userRepository.save(user);

        ExerciseLogRequest logRequest = ExerciseLogRequest.builder()
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .date(LocalDate.now())
                .caloriesBurned(100.0)
                .exerciseCount(10)
                .startTime("120000")
                .endTime("123000")
                .build();

        exerciseLogService.createExerciseLog(logRequest);

        // When / Then
        Assertions.assertThatThrownBy(() -> exerciseService.deleteExercise(exercise.getId()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("이 운동은 운동 로그에 사용되고 있으므로 삭제할 수 없습니다.");
    }


    @Test
    @DisplayName("특정 운동 조회 테스트")
    public void testGetExerciseById() {
        // Given
        ExerciseCategory category = new ExerciseCategory("근력운동");
        exerciseCategoryRepository.save(category);

        Exercise exercise = new Exercise("푸시업", category);
        exerciseRepository.save(exercise);

        // When
        ExerciseResponse response = exerciseService.getExerciseById(exercise.getId());

        // Then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getExerciseName()).isEqualTo("푸시업");
        Assertions.assertThat(response.getExerciseCategoryName()).isEqualTo("근력운동");
    }

    @Test
    @DisplayName("전체 운동 조회 테스트")
    public void testGetAllExercises() {
        // Given
        ExerciseCategory category1 = new ExerciseCategory("근력운동");
        ExerciseCategory category2 = new ExerciseCategory("유산소운동");
        exerciseCategoryRepository.save(category1);
        exerciseCategoryRepository.save(category2);

        Exercise exercise1 = new Exercise("푸시업", category1);
        Exercise exercise2 = new Exercise("런지", category2);
        exerciseRepository.save(exercise1);
        exerciseRepository.save(exercise2);

        // When
        List<ExerciseResponse> exercises = exerciseService.getAllExercises(Optional.empty());

        // Then
        Assertions.assertThat(exercises.size()).isEqualTo(2);
        Assertions.assertThat(exercises).extracting(ExerciseResponse::getExerciseName)
                .containsExactlyInAnyOrder("푸시업", "런지");
    }

}
