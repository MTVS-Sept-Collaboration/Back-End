package com.homefit.backend.exerciselog.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static LocalTime convertStringToLocalTime(String timeString) {
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HHmmss"));
    }
}