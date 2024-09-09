package com.homefit.backend.login.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Hidden // 'Swagger'에서 엔드포인트 숨기기
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "카카오 로그인/회원가입", description = "디버깅용 API")
public class HomeController {

    private final ObjectMapper objectMapper;

    @Operation(summary = "발급받은 액세스 토큰을 디버깅하기 위한 엔드포인트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 디버깅 조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomApiResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 디버깅 조회 실패"),
            @ApiResponse(responseCode = "404", description = "인증 정보를 찾을 수 없음")
    })
    @GetMapping("/")
    public ResponseEntity<?> home(@RequestParam(required = false) String auth_info) {
        if (auth_info != null) {
            try {
                // URL 디코딩
                String decodedAuthInfo = URLDecoder.decode(auth_info, StandardCharsets.UTF_8);

                // JSON 파싱
                Map<String, String> authData = objectMapper.readValue(decodedAuthInfo, new TypeReference<>() {
                });

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
        return ResponseEntity.ok("# 카카오 로그인/회원가입 디버깅용 #");
    }
}