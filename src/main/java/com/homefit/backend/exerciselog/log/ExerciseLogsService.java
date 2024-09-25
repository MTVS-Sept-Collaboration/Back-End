package com.homefit.backend.exerciselog.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ExerciseLogsService {

    private final String logDirectory;

    // 생성자 주입 방식으로 logDirectory 값을 외부 설정에서 가져올 수 있도록 수정
    public ExerciseLogsService(@Value("${log.directory}") String logDirectory) {
        this.logDirectory = logDirectory;
    }

    // 로그 파일에서 엔트리 읽기
    public List<ExerciseLogsEntry> getLogEntries(LocalDate date) {
        String logFileName = getLogFileName(date);
        List<ExerciseLogsEntry> logEntries = new ArrayList<>();

        // 파일 경로를 결합할 때, Paths.get을 사용하여 경로 구분자가 자동으로 추가되도록 수정합니다.
        try (Stream<String> stream = Files.lines(Paths.get(logDirectory, logFileName))) {  // 경로 수정
            stream.forEach(line -> {
                ExerciseLogsEntry entry = parseLogEntry(line);
                if (entry != null) {
                    logEntries.add(entry);
                }
            });
        } catch (IOException e) {
            log.error("로그 파일을 읽을 수 없습니다. 파일명: {}", logFileName, e);
            throw new RuntimeException("로그 파일을 읽을 수 없습니다.", e);
        }

        return logEntries;
    }

    private String getLogFileName(LocalDate date) {
        return "application." + date.toString() + ".log"; // 예: application.2024-09-11.log
    }

    private ExerciseLogsEntry parseLogEntry(String logLine) {
        try {
            // 정규식 패턴을 명확하게 하고, 예외를 처리하여 로그 파싱
            Pattern pattern = Pattern.compile(
                    "\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] (INFO|WARN|ERROR) userId\\s*=\\s*(\\d+) 운동명=([가-힣a-zA-Z]+), 횟수=(\\d+), 소모 칼로리=(\\d+)"
            );
            Matcher matcher = pattern.matcher(logLine);

            if (matcher.find()) {
                String timestampStr = matcher.group(1); // "2024-09-11 10:45:32"
                String logLevel = matcher.group(2);  // "INFO"
                Long userId = Long.parseLong(matcher.group(3)); // 유저 ID
                String exerciseName = matcher.group(4); // 운동 이름
                Integer exerciseCount = Integer.parseInt(matcher.group(5)); // 운동 횟수
                Integer caloriesBurned = Integer.parseInt(matcher.group(6)); // 소모 칼로리

                LocalDate timestamp = LocalDate.parse(timestampStr.substring(0, 10)); // 날짜만 추출

                return new ExerciseLogsEntry(timestamp, logLevel, "ExerciseLog", logLine, userId, exerciseName, exerciseCount, caloriesBurned);
            } else {
                log.warn("로그 라인을 파싱할 수 없습니다: {}", logLine);
                return null;
            }
        } catch (Exception e) {
            log.error("로그 라인 파싱 실패: {}", logLine, e);
            return null;
        }
    }

    // 운동 선호도 수집
    public Map<String, Integer> collectExercisePopularity(List<ExerciseLogsEntry> logEntries) {
        return logEntries.stream()
                .filter(entry -> entry.getExerciseName() != null)
                .collect(Collectors.groupingBy(ExerciseLogsEntry::getExerciseName, Collectors.summingInt(entry -> 1)));
    }

    // 유저별 운동 횟수 수집
    public Map<Long, Integer> collectUserExerciseCounts(List<ExerciseLogsEntry> logEntries) {
        return logEntries.stream()
                .filter(entry -> entry.getUserId() != null && entry.getExerciseCount() != null)
                .collect(Collectors.groupingBy(ExerciseLogsEntry::getUserId, Collectors.summingInt(ExerciseLogsEntry::getExerciseCount)));
    }

    // 전체 운동 횟수 수집
    public int collectTotalExerciseCount(List<ExerciseLogsEntry> logEntries) {
        return logEntries.stream()
                .filter(entry -> entry.getExerciseCount() != null)
                .mapToInt(ExerciseLogsEntry::getExerciseCount)
                .sum();
    }

    // 날짜별 운동 기록 수집
    public Map<LocalDate, List<String>> collectExerciseByDate(List<ExerciseLogsEntry> logEntries) {
        return logEntries.stream()
                .filter(entry -> entry.getExerciseName() != null)
                .collect(Collectors.groupingBy(ExerciseLogsEntry::getTimestamp,
                        Collectors.mapping(ExerciseLogsEntry::getExerciseName, Collectors.toList())));
    }

    // 운동 카운트 수집
    public Map<Long, Map<String, Integer>> collectExerciseCounts(List<ExerciseLogsEntry> logEntries) {
        Map<Long, Map<String, Integer>> userExerciseData = new HashMap<>();

        for (ExerciseLogsEntry entry : logEntries) {
            if (entry.getUserId() != null && entry.getExerciseName() != null && entry.getExerciseCount() != null) {
                userExerciseData
                        .computeIfAbsent(entry.getUserId(), k -> new HashMap<>())
                        .merge(entry.getExerciseName(), entry.getExerciseCount(), Integer::sum);
            }
        }

        return userExerciseData;
    }
}
