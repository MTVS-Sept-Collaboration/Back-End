package com.homefit.backend.ranking.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class RankingKeyUtil {

    // 일간 key 생성 (예: user_ranking:daily:20240922)
    public static String getDailyRankingKey() {
        LocalDate today = LocalDate.now();
        return "user_ranking:daily:" + today.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    // 주간 key 생성 (예: user_ranking:weekly:2024-W38)
    public static String getWeeklyRankingKey() {
        
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekOfYear = today.get(weekFields.weekOfWeekBasedYear());
        return "user_ranking:weekly:" + today.getYear() + "-W" + weekOfYear;
    }

    // 월간 key 생성 (예: user_ranking:monthly:202409)
    public static String getMonthlyRankingKey() {
        LocalDate today = LocalDate.now();
        return "user_ranking:monthly:" + today.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }
}
