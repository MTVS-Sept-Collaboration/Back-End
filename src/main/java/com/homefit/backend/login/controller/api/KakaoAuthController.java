package com.homefit.backend.login.controller.api;

import com.homefit.backend.login.common.CustomApiResponse;
import com.homefit.backend.login.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "카카오 로그인/회원가입", description = "카카오 인증 관련 API")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "카카오 로그인 인증 URL 반환")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @GetMapping("/kakao/login")
    public ResponseEntity<CustomApiResponse<String>> getKakaoLoginUrl(HttpServletRequest request) {
        String kakaoLoginUrl = kakaoAuthService.getKakaoLoginUrl(request);
        return CustomApiResponse.success("loginUrl", kakaoLoginUrl);
    }
}
