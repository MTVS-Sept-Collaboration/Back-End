package com.homefit.backend.exerciselog.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeUtilsTest {

    @Test
    @DisplayName("올바른 시간 문자열을 LocalTime으로 변환하는 테스트")
    public void testConvertStringToLocalTime_Success() {
        // Given
        String timeString = "12:30:45";

        // When
        LocalTime result = TimeUtils.convertStringToLocalTime(timeString);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(LocalTime.of(12, 30, 45), result);
    }

    @Test
    @DisplayName("잘못된 시간 형식으로 변환 시 예외 발생 테스트")
    public void testConvertStringToLocalTime_InvalidFormat() {
        // Given
        String invalidTimeString = "12:30";  // Invalid format

        // When & Then
        Assertions.assertThrows(DateTimeParseException.class, () -> {
            TimeUtils.convertStringToLocalTime(invalidTimeString);
        });
    }

    @Test
    @DisplayName("빈 문자열로 변환 시 예외 발생 테스트")
    public void testConvertStringToLocalTime_EmptyString() {
        // Given
        String emptyTimeString = "";

        // When & Then
        Assertions.assertThrows(DateTimeParseException.class, () -> {
            TimeUtils.convertStringToLocalTime(emptyTimeString);
        });
    }

    @Test
    @DisplayName("널 값으로 변환 시 예외 발생 테스트")
    public void testConvertStringToLocalTime_NullInput() {
        // Given
        String nullTimeString = null;

        // When & Then
        Assertions.assertThrows(NullPointerException.class, () -> {
            TimeUtils.convertStringToLocalTime(nullTimeString);
        });
    }
}
