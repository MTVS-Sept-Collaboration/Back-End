package com.homefit.backend.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogManagementService {

    private static final String LOG_FILE_PATH = "logs/application.log";
    private final ObjectMapper objectMapper;

    public LogResponse getLogsAsJson(int page, int limit, String sortOrder, String logLevel) throws IOException {
        List<LogEntry> allLogs = Files.readAllLines(Paths.get(LOG_FILE_PATH)).stream()
                .map(this::parseLogEntry)
                .filter(log -> logLevel == null || logLevel.isEmpty() || logLevel.equalsIgnoreCase(log.level))
                .collect(Collectors.toList());

        if ("desc".equalsIgnoreCase(sortOrder)) {
            Collections.reverse(allLogs);
        }

        int totalLogs = allLogs.size();
        int totalPages = (int) Math.ceil((double) totalLogs / limit);
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, totalLogs);

//        if (startIndex >= totalLogs) {
//            startIndex = 0;
//            page = 1;
//        }

        List<LogEntry> paginatedLogs = allLogs.subList(startIndex, endIndex);
        return new LogResponse(paginatedLogs, page, totalPages, totalLogs);
    }

    private LogEntry parseLogEntry(String logLine) {
        String[] parts = logLine.split(" ", 4);
        if (parts.length < 4) {
            return new LogEntry("Unknown", "Unknown", "Unknown", "Unknown", logLine);
        }
        String timestamp = parts[0] + " " + parts[1];
        String level = parts[2];

        // 로거와 메시지 부분을 분리합니다.
        String[] loggerAndMessage = parts[3].split(" - ", 2);
        String fullLogger = loggerAndMessage[0];
        String shortLogger = fullLogger.substring(fullLogger.lastIndexOf('.') + 1);
        String message = loggerAndMessage.length > 1 ? loggerAndMessage[1] : "";

        return new LogEntry(timestamp, level, fullLogger, shortLogger, message);
    }

    public static class LogEntry {
        public String timestamp;
        public String level;
        public String fullLogger;
        public String shortLogger;
        public String message;

        public LogEntry(String timestamp, String level, String fullLogger, String shortLogger, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.fullLogger = fullLogger;
            this.shortLogger = shortLogger;
            this.message = message;
        }
    }

    public static class LogResponse {
        public List<LogEntry> logs;
        public int currentPage;
        public int totalPages;
        public int totalLogs;

        public LogResponse(List<LogEntry> logs, int currentPage, int totalPages, int totalLogs) {
            this.logs = logs;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalLogs = totalLogs;
        }
    }
}