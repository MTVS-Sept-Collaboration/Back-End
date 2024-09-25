package com.homefit.backend.exerciselog.service;


import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.repository.ExerciseCategoryRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.repository.ExerciseLogRepository;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Transactional
public class ExerciseLogServiceTest {

    @Autowired
    private ExerciseLogService exerciseLogService;

    @Autowired
    private ExerciseLogRepository exerciseLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;

    private User user;
    private Exercise exercise1;
    private Exercise exercise2;

    @BeforeEach
    public void setUp() {
        // User 객체 생성 및 저장
        user = userRepository.save(User.builder()
                .userName("testUser")
                .password("password123")
                .role(RoleType.USER) // 역할 설정
                .build());

        // ExerciseCategory 생성
        ExerciseCategory category = exerciseCategoryRepository.save(new ExerciseCategory("Fitness"));

        // Exercise 객체 생성 및 저장
        exercise1 = exerciseRepository.save(new Exercise("Push-up", category));
        exercise2 = exerciseRepository.save(new Exercise("Sit-up", category));
    }

    @Test
    @DisplayName("운동 로그 생성 테스트")
    public void testCreateExerciseLog() {
        // Given
        ExerciseLogRequest request = ExerciseLogRequest.builder()
                .date(LocalDate.now())  // LocalDate로 변경
                .caloriesBurned(300.5)
                .exerciseCount(2)
                .startTime(LocalTime.of(10, 15, 30))  // 시작 시간 설정
                .endTime(LocalTime.of(11, 45, 30))    // 끝 시간 설정
                .userId(user.getId())
                .exerciseId(exercise1.getId())  // 단일 exerciseId로 변경
                .build();

        // When
        ExerciseLogResponse response = exerciseLogService.createExerciseLog(request);

        // Then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getExerciseCount());
        Assertions.assertEquals(300.5, response.getCaloriesBurned());
        Assertions.assertEquals(exercise1.getId(), response.getExerciseId());  // 운동 ID 체크
    }

    @Test
    @DisplayName("운동 로그 수정 테스트")
    public void testUpdateExerciseLog() {
        // Given
        ExerciseLogRequest createRequest = ExerciseLogRequest.builder()
                .date(LocalDate.now())
                .caloriesBurned(200.0)
                .exerciseCount(1)
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(10, 30))
                .userId(user.getId())
                .exerciseId(exercise1.getId())
                .build();
        ExerciseLogResponse createdLog = exerciseLogService.createExerciseLog(createRequest);

        ExerciseLogRequest updateRequest = ExerciseLogRequest.builder()
                .date(LocalDate.now())
                .caloriesBurned(350.0)
                .exerciseCount(2)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .userId(user.getId())
                .exerciseId(exercise2.getId())  // 운동을 exercise2로 변경
                .build();

        // When
        ExerciseLogResponse updatedLog = exerciseLogService.updateExerciseLog(createdLog.getId(), user.getId(), updateRequest);

        // Then
        Assertions.assertNotNull(updatedLog);
        Assertions.assertEquals(2, updatedLog.getExerciseCount());
        Assertions.assertEquals(350.0, updatedLog.getCaloriesBurned());
        Assertions.assertEquals(exercise2.getId(), updatedLog.getExerciseId());  // 운동 ID 체크
    }

    @Test
    @DisplayName("운동 로그 삭제 테스트")
    public void testDeleteExerciseLog() {
        // Given
        ExerciseLogRequest request = ExerciseLogRequest.builder()
                .date(LocalDate.now())
                .caloriesBurned(250.0)
                .exerciseCount(1)
                .startTime(LocalTime.of(8, 30))
                .endTime(LocalTime.of(9, 30))
                .userId(user.getId())
                .exerciseId(exercise1.getId())
                .build();
        ExerciseLogResponse createdLog = exerciseLogService.createExerciseLog(request);

        // When
        exerciseLogService.deleteExerciseLog(createdLog.getId(), user.getId());

        // Then
        Assertions.assertFalse(exerciseLogRepository.existsById(createdLog.getId()));
    }

    @Test
    @DisplayName("특정 유저의 운동 로그 조회 테스트")
    public void testGetExerciseLogsByUser() {
        // Given
        ExerciseLogRequest request = ExerciseLogRequest.builder()
                .date(LocalDate.now())
                .caloriesBurned(200.0)
                .exerciseCount(1)
                .startTime(LocalTime.of(7, 30))
                .endTime(LocalTime.of(8, 30))
                .userId(user.getId())
                .exerciseId(exercise1.getId())
                .build();
        exerciseLogService.createExerciseLog(request);

        // When
        List<ExerciseLogResponse> logs = exerciseLogService.getExerciseLogsByUser(user.getId());

        // Then
        Assertions.assertNotNull(logs);
        Assertions.assertEquals(1, logs.size());
    }

    @Test
    @DisplayName("특정 날짜의 유저 운동 로그 조회 테스트 (합산된 결과)")
    public void testGetExerciseLogsByUserAndDate() {
        // Given
        LocalDate date = LocalDate.now();  // 오늘 날짜로 설정

        // 첫 번째 운동 기록 생성
        ExerciseLogRequest request1 = ExerciseLogRequest.builder()
                .date(date)
                .caloriesBurned(250.0)
                .exerciseCount(1)
                .startTime(LocalTime.of(6, 30))
                .endTime(LocalTime.of(7, 30))
                .userId(user.getId())
                .exerciseId(exercise1.getId())
                .build();
        exerciseLogService.createExerciseLog(request1);

        // 두 번째 운동 기록 생성
        ExerciseLogRequest request2 = ExerciseLogRequest.builder()
                .date(date)
                .caloriesBurned(150.0)
                .exerciseCount(2)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 45))
                .userId(user.getId())
                .exerciseId(exercise2.getId())
                .build();
        exerciseLogService.createExerciseLog(request2);

        // When
        ExerciseLogResponse response = exerciseLogService.getTotalExerciseLogsByUserAndDate(user.getId(), date);

        // Then
        Assertions.assertNotNull(response);  // 응답이 null이 아닌지 확인
        Assertions.assertEquals(400.0, response.getCaloriesBurned());  // 총 소모된 칼로리 확인 (250 + 150)
        Assertions.assertEquals(3, response.getExerciseCount());  // 총 운동 횟수 확인 (1 + 2)
    }

}

