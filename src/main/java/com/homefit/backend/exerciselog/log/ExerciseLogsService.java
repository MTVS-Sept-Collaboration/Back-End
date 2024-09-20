package com.homefit.backend.exerciselog.log;

import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Stream;

@Slf4j
@Service
public class ExerciseLogsService {

    private final String logDirectory = "logs/";

    public List<ExerciseLogsEntry> getLogEntries(LocalDate date) {
        String logFileName = getLogFileName(date);
        List<ExerciseLogsEntry> logEntries = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(logDirectory + logFileName))) {
            stream.forEach(line -> {
                ExerciseLogsEntry entry = parseLogEntry(line);
                if (entry != null) {
                    logEntries.add(entry);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("로그 파일을 읽을 수 없습니다.", e);
        }

        return logEntries;
    }

    private String getLogFileName(LocalDate date) {
        return "application." + date.toString() + ".log"; // 예: application.2024-09-11.log
    }

    private ExerciseLogsEntry parseLogEntry(String logLine) {
        try {
            // 정규식을 사용하여 로그 라인 전체에서 필요한 데이터를 추출
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

                // String으로 받은 timestamp를 LocalDate로 변환 (시간은 무시하고 날짜만 사용)
                LocalDate timestamp = LocalDate.parse(timestampStr.substring(0, 10)); // "2024-09-11"

                // 로그 엔트리 객체 생성 후 반환
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



    // 운동 선호도 수집: 운동 이름별로 얼마나 자주 수행되었는지 집계
    public Map<String, Integer> collectExercisePopularity(List<ExerciseLogsEntry> logEntries) {
        Map<String, Integer> exercisePopularity = new HashMap<>();

        for (ExerciseLogsEntry entry : logEntries) {
            String exerciseName = entry.getExerciseName();
            if (exerciseName != null) {
                exercisePopularity.put(exerciseName, exercisePopularity.getOrDefault(exerciseName, 0) + 1);
            }
        }

        return exercisePopularity;
    }

    // 유저별 운동 횟수 수집
    public Map<Long, Integer> collectUserExerciseCounts(List<ExerciseLogsEntry> logEntries) {
        Map<Long, Integer> userExerciseCounts = new HashMap<>();

        for (ExerciseLogsEntry entry : logEntries) {
            Long userId = entry.getUserId();
            Integer exerciseCount = entry.getExerciseCount();
            if (userId != null && exerciseCount != null) {
                userExerciseCounts.put(userId, userExerciseCounts.getOrDefault(userId, 0) + exerciseCount);
            }
        }

        return userExerciseCounts;
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
        Map<LocalDate, List<String>> exercisesByDate = new HashMap<>();

        for (ExerciseLogsEntry entry : logEntries) {
            String exerciseName = entry.getExerciseName();
            LocalDate date = entry.getTimestamp();  // 날짜는 이미 LocalDate로 저장됨

            if (exerciseName != null) {
                exercisesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(exerciseName);
            }
        }

        return exercisesByDate;
    }

    // 운동 카운트 수집
    public Map<Long, Map<String, Integer>> collectExerciseCounts(List<ExerciseLogsEntry> logEntries) {
        Map<Long, Map<String, Integer>> userExerciseData = new HashMap<>();

        for (ExerciseLogsEntry entry : logEntries) {
            Long userId = entry.getUserId();
            String exerciseName = entry.getExerciseName();
            Integer exerciseCount = entry.getExerciseCount();

            if (userId != null && exerciseName != null && exerciseCount != null) {
                userExerciseData.putIfAbsent(userId, new HashMap<>());
                Map<String, Integer> exerciseData = userExerciseData.get(userId);
                exerciseData.put(exerciseName, exerciseData.getOrDefault(exerciseName, 0) + exerciseCount);
            }
        }

        return userExerciseData;
    }


}
