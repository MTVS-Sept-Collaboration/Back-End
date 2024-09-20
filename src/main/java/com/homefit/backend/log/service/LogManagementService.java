package com.homefit.backend.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogManagementService {

    private static final String LOG_FILE_PATH = "logs/application.log";
    private final ObjectMapper objectMapper;

    public LogManagementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> getRecentLogs(int limit) throws IOException {
        List<String> logs = Files.readAllLines(Paths.get(LOG_FILE_PATH));
        int startIndex = Math.max(0, logs.size() - limit);
        return logs.subList(startIndex, logs.size());
    }

    public String getLogsAsJson(int limit) throws IOException {
        List<String> recentLogs = getRecentLogs(limit);
        List<LogEntry> logEntries = recentLogs.stream()
                .map(this::parseLogEntry)
                .collect(Collectors.toList());
        return objectMapper.writeValueAsString(logEntries);
    }

    private LogEntry parseLogEntry(String logLine) {
        // 간단한 파싱 로직. 실제 로그 형식에 맞게 조정 필요
        String[] parts = logLine.split(" ", 5);
        if (parts.length < 5) {
            return new LogEntry("Unknown", "Unknown", "Unknown", logLine);
        }
        return new LogEntry(parts[0] + " " + parts[1], parts[2], parts[3], parts[4]);
    }

    private static class LogEntry {
        public String timestamp;
        public String level;
        public String logger;
        public String message;

        public LogEntry(String timestamp, String level, String logger, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.logger = logger;
            this.message = message;
        }
    }
}