package com.homefit.backend.exerciselog;


import com.homefit.backend.exercise.entity.Exercise;
import com.homefit.backend.exercise.repository.ExerciseRepository;
import com.homefit.backend.category.exercise.entity.ExerciseCategory;
import com.homefit.backend.category.exercise.repository.ExerciseCategoryRepository;
import com.homefit.backend.exerciselog.dto.ExerciseLogRequest;
import com.homefit.backend.exerciselog.dto.ExerciseLogResponse;
import com.homefit.backend.exerciselog.repository.ExerciseLogRepository;
import com.homefit.backend.exerciselog.service.ExerciseLogService;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.repository.UserRepository;
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
                .id(null) // ID는 자동 생성
                .kakaoId("kakao123")
                .nickName("testUser")
                .birthday(LocalDate.of(1990, 1, 1)) // 생년월일
                .profileImage("https://example.com/profile.jpg")
                .role(RoleType.USER) // 역할 설정
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .firedAt(null) // 퇴사일 없음
                .userStatus(true) // 활성화 상태
                .refreshToken("refreshTokenExample")
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
                .date(LocalDateTime.now())
                .caloriesBurned(300.5)
                .exerciseCount(2)
                .exerciseTime(Duration.ofMinutes(30))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId(), exercise2.getId()))
                .build();

        // When
        ExerciseLogResponse response = exerciseLogService.createExerciseLog(request);

        // Then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getExerciseCount());
        Assertions.assertEquals(2, response.getExerciseNames().size());
        Assertions.assertEquals(300.5, response.getCaloriesBurned());
    }

    @Test
    @DisplayName("운동 로그 수정 테스트")
    public void testUpdateExerciseLog() {
        // Given
        ExerciseLogRequest createRequest = ExerciseLogRequest.builder()
                .date(LocalDateTime.now())
                .caloriesBurned(200.0)
                .exerciseCount(1)
                .exerciseTime(Duration.ofMinutes(20))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId()))
                .build();
        ExerciseLogResponse createdLog = exerciseLogService.createExerciseLog(createRequest);

        ExerciseLogRequest updateRequest = ExerciseLogRequest.builder()
                .date(LocalDateTime.now())
                .caloriesBurned(350.0)
                .exerciseCount(2)
                .exerciseTime(Duration.ofMinutes(40))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId(), exercise2.getId()))
                .build();

        // When
        ExerciseLogResponse updatedLog = exerciseLogService.updateExerciseLog(createdLog.getId(), user.getId(), updateRequest);

        // Then
        Assertions.assertNotNull(updatedLog);
        Assertions.assertEquals(2, updatedLog.getExerciseCount());
        Assertions.assertEquals(350.0, updatedLog.getCaloriesBurned());
    }

    @Test
    @DisplayName("운동 로그 삭제 테스트")
    public void testDeleteExerciseLog() {
        // Given
        ExerciseLogRequest request = ExerciseLogRequest.builder()
                .date(LocalDateTime.now())
                .caloriesBurned(250.0)
                .exerciseCount(1)
                .exerciseTime(Duration.ofMinutes(25))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId()))
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
                .date(LocalDateTime.now())
                .caloriesBurned(200.0)
                .exerciseCount(1)
                .exerciseTime(Duration.ofMinutes(20))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId()))
                .build();
        exerciseLogService.createExerciseLog(request);

        // When
        List<ExerciseLogResponse> logs = exerciseLogService.getExerciseLogsByUser(user.getId());

        // Then
        Assertions.assertNotNull(logs);
        Assertions.assertEquals(1, logs.size());
    }

    @Test
    @DisplayName("특정 날짜의 유저 운동 로그 조회 테스트")
    public void testGetExerciseLogsByUserAndDate() {
        // Given
        LocalDate date = LocalDate.now();  // 오늘 날짜로 설정

        ExerciseLogRequest request = ExerciseLogRequest.builder()
                .date(LocalDateTime.now())  // 운동 로그는 여전히 LocalDateTime을 사용할 수 있음
                .caloriesBurned(250.0)
                .exerciseCount(1)
                .exerciseTime(Duration.ofMinutes(25))
                .userId(user.getId())
                .exerciseIds(List.of(exercise1.getId()))
                .build();
        exerciseLogService.createExerciseLog(request);

        // When
        List<ExerciseLogResponse> logs = exerciseLogService.getExerciseLogsByUserAndDate(user.getId(), date);

        // Then
        Assertions.assertNotNull(logs);
        Assertions.assertFalse(logs.isEmpty());
        Assertions.assertEquals(1, logs.size());
    }



}
