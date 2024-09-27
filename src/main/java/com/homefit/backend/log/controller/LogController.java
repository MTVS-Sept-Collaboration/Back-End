package com.homefit.backend.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.log.service.LogManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "로그 API", description = "로그 조회 API")
public class LogController {

    private final LogManagementService logManagementService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "로그 조회", description = "페이지네이션, 정렬, 필터링을 적용하여 로그를 JSON 형태로 반환합니다.")
    @GetMapping
    public ResponseEntity<String> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String logLevel) {
        try {
            LogManagementService.LogResponse logResponse = logManagementService.getLogsAsJson(page, limit, sortOrder, logLevel);
            String logs = objectMapper.writeValueAsString(logResponse);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("로그를 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
