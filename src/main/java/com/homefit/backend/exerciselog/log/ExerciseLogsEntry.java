package com.homefit.backend.exerciselog.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseLogsEntry {
    private LocalDate timestamp;      // 로그의 발생 시간
    private String logLevel;       // 로그 레벨 (INFO, ERROR 등)
    private String loggerName;     // 로거 이름
    private String message;        // 로그 메시지
    private Long userId;           // 유저 ID (로그에서 찾을 수 있는 경우)
    private String exerciseName;   // 운동 이름 (로그에 있는 경우)
    private Integer exerciseCount; // 운동 횟수 (로그에 있는 경우)
    private Integer caloriesBurned; // 소모 칼로리 (로그에 있는 경우)
}
