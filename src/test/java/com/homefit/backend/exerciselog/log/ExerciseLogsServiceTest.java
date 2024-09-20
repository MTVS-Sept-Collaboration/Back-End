package com.homefit.backend.exerciselog.log;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ExerciseLogsServiceTest {

    @Autowired
    private ExerciseLogsService exerciseLogsService;

    private final String logDirectory = "logs/";
    private final LocalDate testDate = LocalDate.of(2024, 9, 11);

    // 테스트 전 더미 로그 파일 생성
    @BeforeEach
    void setupDummyLogFile() throws IOException {
        // 로그 파일 경로 설정
        Path logFilePath = Paths.get(logDirectory + "application." + testDate + ".log");

        // 더미 로그 데이터 생성
        String dummyLogData = """
        [2024-09-11 10:45:32] INFO userId=123 운동명=Pushup, 횟수=20, 소모 칼로리=100
        [2024-09-11 11:00:00] INFO userId=124 운동명=Squat, 횟수=15, 소모 칼로리=90
        [2024-09-11 12:15:45] INFO userId=125 운동명=Burpee, 횟수=30, 소모 칼로리=200
        """;

        // 로그 파일 디렉토리 생성 (없으면)
        Files.createDirectories(logFilePath.getParent());

        // 기존 파일 삭제 후 더미 파일 작성
        Files.deleteIfExists(logFilePath);
        Files.write(logFilePath, dummyLogData.getBytes(), StandardOpenOption.CREATE);
    }

    @Test
    void testExtractUserId() {
        String[] testMessages = {
                "UserId=123 운동명=Pushup, 횟수=20 소모 칼로리=100",
                "userId = 456 운동명=Squat, 횟수=15 소모 칼로리=90",
                "User Id=789 운동명=Burpee, 횟수=30 소모 칼로리=200",
                "user Id=101112 운동명=Plank, 횟수=60 소모 칼로리=50"
        };

        Pattern pattern = Pattern.compile("\\b(User\\s*Id|user\\s*Id|UserId|userId)\\s*=\\s*(\\d+)\\b");

        for (String message : testMessages) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String userId = matcher.group(2);
                System.out.println("추출된 userId: " + userId);
            } else {
                throw new AssertionError("userId not found in message: " + message);
            }
        }
    }


    @Test
    @DisplayName("로그 엔트리 목록을 가져오는 기능 테스트 (더미 데이터)")
    void testGetLogEntriesWithDummyData() {
        // when
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // then
        assertNotNull(logEntries);
        assertEquals(3, logEntries.size(), "로그 엔트리가 3개여야 합니다.");
    }

    @Test
    @DisplayName("운동 선호도 데이터를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExercisePopularityWithDummyData() {
        // given
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // when
        Map<String, Integer> exercisePopularity = exerciseLogsService.collectExercisePopularity(logEntries);

        // then
        assertNotNull(exercisePopularity);
        assertEquals(3, exercisePopularity.size(), "운동 선호도 데이터는 3개여야 합니다.");
        assertEquals(1, exercisePopularity.get("Pushup"));
        assertEquals(1, exercisePopularity.get("Squat"));
        assertEquals(1, exercisePopularity.get("Burpee"));
    }

    @Test
    @DisplayName("유저별 운동 횟수를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectUserExerciseCountsWithDummyData() {
        // given
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // when
        Map<Long, Integer> userExerciseCounts = exerciseLogsService.collectUserExerciseCounts(logEntries);

        // then
        assertNotNull(userExerciseCounts);
        assertEquals(3, userExerciseCounts.size(), "유저별 운동 횟수 데이터가 3명이어야 합니다.");
        assertEquals(20, userExerciseCounts.get(123L));
        assertEquals(15, userExerciseCounts.get(124L));
        assertEquals(30, userExerciseCounts.get(125L));
    }

    @Test
    @DisplayName("전체 운동 횟수를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectTotalExerciseCountWithDummyData() {
        // given
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // when
        int totalExerciseCount = exerciseLogsService.collectTotalExerciseCount(logEntries);

        // then
        assertEquals(65, totalExerciseCount, "전체 운동 횟수는 65여야 합니다.");
    }

    @Test
    @DisplayName("날짜별 운동 기록을 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExerciseByDateWithDummyData() {
        // given
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // when
        Map<LocalDate, List<String>> exerciseByDate = exerciseLogsService.collectExerciseByDate(logEntries);

        // then
        assertNotNull(exerciseByDate);
        assertEquals(3, exerciseByDate.get(testDate).size(), "날짜별 운동 기록 데이터는 3개여야 합니다.");
        assertTrue(exerciseByDate.get(testDate).contains("Pushup"));
        assertTrue(exerciseByDate.get(testDate).contains("Squat"));
        assertTrue(exerciseByDate.get(testDate).contains("Burpee"));
    }

    @Test
    @DisplayName("유저별 운동 카운트 데이터를 수집하는 기능 테스트 (더미 데이터)")
    void testCollectExerciseCountsWithDummyData() {
        // given
        List<ExerciseLogsEntry> logEntries = exerciseLogsService.getLogEntries(testDate);

        // when
        Map<Long, Map<String, Integer>> exerciseCounts = exerciseLogsService.collectExerciseCounts(logEntries);

        // then
        assertNotNull(exerciseCounts);
        assertEquals(3, exerciseCounts.size(), "유저별 운동 카운트 데이터는 3명이어야 합니다.");
        assertEquals(20, exerciseCounts.get(123L).get("Pushup"));
        assertEquals(15, exerciseCounts.get(124L).get("Squat"));
        assertEquals(30, exerciseCounts.get(125L).get("Burpee"));
    }

}