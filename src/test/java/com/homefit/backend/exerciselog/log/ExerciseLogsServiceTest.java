package com.homefit.backend.exerciselog.log;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
public class ExerciseLogsServiceTest {

    @Autowired
    private ExerciseLogsService exerciseLogsService;

    private final LocalDate testDate = LocalDate.of(2024, 9, 11);

    // 테스트 전 더미 로그 파일 생성
    @BeforeEach
    void setupDummyLogFile() throws IOException {
        String logDirectory = "logs/";
        Path logFilePath = Paths.get(logDirectory + "application." + testDate + ".log");

        String dummyLogData = """
        [2024-09-11 10:45:32] INFO userId=123 운동명=Pushup, 횟수=20, 소모 칼로리=100
        [2024-09-11 11:00:00] INFO userId=124 운동명=Squat, 횟수=15, 소모 칼로리=90
        [2024-09-11 12:15:45] INFO userId=125 운동명=Burpee, 횟수=30, 소모 칼로리=200
        """;

        Files.createDirectories(logFilePath.getParent());
        Files.deleteIfExists(logFilePath);
        Files.write(logFilePath, dummyLogData.getBytes(), StandardOpenOption.CREATE);
    }

    @Test
    @DisplayName("로그 엔트리 목록을 가져오는 기능 테스트 (더미 데이터)")
    void testGetLogEntriesWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // 로그 출력
        logEntries.forEach(entry -> log.info("로그 엔트리: {}", entry));

        // then
        assertNotNull(logEntries, "로그 엔트리 목록이 null이 아니어야 합니다.");
        assertEquals(3, logEntries.size(), "로그 엔트리가 3개여야 합니다.");
    }

    @Test
    @DisplayName("운동 선호도 데이터를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExercisePopularityWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        Map<String, Integer> exercisePopularity = exerciseLogsService.collectExercisePopularity(logEntries);

        // 로그 출력
        exercisePopularity.forEach((exercise, count) -> log.info("운동명: {}, 횟수: {}", exercise, count));

        assertNotNull(exercisePopularity, "운동 선호도 데이터가 null이 아니어야 합니다.");
        assertEquals(3, exercisePopularity.size(), "운동 선호도 데이터는 3개여야 합니다.");
        assertEquals(1, exercisePopularity.get("Pushup"));
        assertEquals(1, exercisePopularity.get("Squat"));
        assertEquals(1, exercisePopularity.get("Burpee"));
    }

    @Test
    @DisplayName("유저별 운동 횟수를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectUserExerciseCountsWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        Map<Long, Integer> userExerciseCounts = exerciseLogsService.collectUserExerciseCounts(logEntries);

        // 로그 출력
        userExerciseCounts.forEach((userId, count) -> log.info("유저 ID: {}, 운동 횟수: {}", userId, count));

        assertNotNull(userExerciseCounts, "유저별 운동 횟수 데이터가 null이 아니어야 합니다.");
        assertEquals(3, userExerciseCounts.size(), "유저별 운동 횟수 데이터가 3명이어야 합니다.");
        assertEquals(20, userExerciseCounts.get(123L), "유저 123의 운동 횟수는 20이어야 합니다.");
        assertEquals(15, userExerciseCounts.get(124L), "유저 124의 운동 횟수는 15이어야 합니다.");
        assertEquals(30, userExerciseCounts.get(125L), "유저 125의 운동 횟수는 30이어야 합니다.");
    }

    @Test
    @DisplayName("전체 운동 횟수를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectTotalExerciseCountWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        int totalExerciseCount = exerciseLogsService.collectTotalExerciseCount(logEntries);

        // 로그 출력
        log.info("전체 운동 횟수: {}", totalExerciseCount);

        assertEquals(65, totalExerciseCount, "전체 운동 횟수는 65여야 합니다.");
    }

    @Test
    @DisplayName("날짜별 운동 기록을 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExerciseByDateWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        Map<LocalDate, List<String>> exerciseByDate = exerciseLogsService.collectExerciseByDate(logEntries);

        // 로그 출력
        exerciseByDate.forEach((date, exercises) -> log.info("날짜: {}, 운동: {}", date, exercises));

        assertNotNull(exerciseByDate, "날짜별 운동 기록 데이터가 null이 아니어야 합니다.");
        assertEquals(3, exerciseByDate.get(testDate).size(), "날짜별 운동 기록 데이터는 3개여야 합니다.");
        assertTrue(exerciseByDate.get(testDate).contains("Pushup"));
        assertTrue(exerciseByDate.get(testDate).contains("Squat"));
        assertTrue(exerciseByDate.get(testDate).contains("Burpee"));
    }

    @Test
    @DisplayName("유저별 운동 카운트 데이터를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExerciseCountsWithDummyData() {
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        Map<Long, Map<String, Integer>> exerciseCounts = exerciseLogsService.collectExerciseCounts(logEntries);

        // 로그 출력
        exerciseCounts.forEach((userId, exercises) -> {
            exercises.forEach((exercise, count) -> log.info("유저 ID: {}, 운동명: {}, 횟수: {}", userId, exercise, count));
        });

        assertNotNull(exerciseCounts, "유저별 운동 카운트 데이터가 null이 아니어야 합니다.");
        assertEquals(3, exerciseCounts.size(), "유저별 운동 카운트 데이터는 3명이어야 합니다.");
        assertEquals(20, exerciseCounts.get(123L).get("Pushup"), "Pushup 운동 횟수는 20이어야 합니다.");
        assertEquals(15, exerciseCounts.get(124L).get("Squat"), "Squat 운동 횟수는 15이어야 합니다.");
        assertEquals(30, exerciseCounts.get(125L).get("Burpee"), "Burpee 운동 횟수는 30이어야 합니다.");
    }
}