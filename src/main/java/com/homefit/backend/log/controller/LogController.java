package com.homefit.backend.log.controller;

import com.homefit.backend.log.service.LogManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "로그 API", description = "로그 조회 API")
public class LogController {

    private final LogManagementService logManagementService;

    public LogController(LogManagementService logManagementService) {
        this.logManagementService = logManagementService;
    }

    @Operation(summary = "최근 로그 조회", description = "지정된 개수만큼의 최근 로그를 JSON 형태로 반환합니다.")
    @GetMapping
    public ResponseEntity<String> getLogs(@RequestParam(defaultValue = "100") int limit) {
        try {
            String logs = logManagementService.getLogsAsJson(limit);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("로그를 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
