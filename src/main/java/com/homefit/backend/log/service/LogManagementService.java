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

    public String getLogsAsJson(int page, int limit, String sortOrder, String logLevel) throws IOException {
        List<LogEntry> allLogs = Files.readAllLines(Paths.get(LOG_FILE_PATH)).stream()
                .map(this::parseLogEntry)
                .filter(log -> logLevel == null || logLevel.isEmpty() || logLevel.equals(log.level))
                .collect(Collectors.toList());

        if ("desc".equalsIgnoreCase(sortOrder)) {
            Collections.reverse(allLogs);
        }

        int totalLogs = allLogs.size();
        int totalPages = (int) Math.ceil((double) totalLogs / limit);
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, totalLogs);

        List<LogEntry> paginatedLogs = allLogs.subList(startIndex, endIndex);

        LogResponse response = new LogResponse(paginatedLogs, page, totalPages, totalLogs);
        return objectMapper.writeValueAsString(response);
    }

    private LogEntry parseLogEntry(String logLine) {
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

    private static class LogResponse {
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