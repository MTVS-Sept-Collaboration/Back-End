package com.homefit.backend.login.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HomeController {

    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public ResponseEntity<?> home(@RequestParam(required = false) String auth_info) {
        if (auth_info != null) {
            try {
                // URL 디코딩
                String decodedAuthInfo = URLDecoder.decode(auth_info, StandardCharsets.UTF_8);

                // JSON 파싱
                Map<String, String> authData = objectMapper.readValue(decodedAuthInfo, new TypeReference<Map<String, String>>() {});

                // 응답 데이터 구성
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("userId", authData.get("userId"));
                response.put("accessToken", authData.get("accessToken"));

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error processing auth_info", e);
                return ResponseEntity.badRequest().body("Invalid auth_info format: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("Welcome to the API server");
    }
}