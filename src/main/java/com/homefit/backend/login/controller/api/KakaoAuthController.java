package com.homefit.backend.login.controller.api;

import com.homefit.backend.login.common.ApiResponse;
import com.homefit.backend.login.dto.KakaoLoginRequest;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Kakao Authentication")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "#1. 카카오 인증 코드로 액세스 토큰 발급")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404")
    })
    @GetMapping("/kakao/token")
    public ResponseEntity<ApiResponse<String>> getKakaoAccessToken(@RequestParam String code) {
        String accessToken = kakaoAuthService.getKakaoAccessToken(code);
        return ApiResponse.success("accessToken", accessToken);
    }

    @Operation(summary = "#2. 카카오 액세스 토큰으로 사용자 정보 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404")
    })
    @GetMapping("/kakao/user-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getKakaoUserInfo(@RequestParam String accessToken) {
        Map<String, Object> userInfo = kakaoAuthService.getKakaoUserInfo(accessToken);
        return ApiResponse.success("userInfo", userInfo);
    }

    @Operation(summary = "#3. 발급받은 액세스 토큰으로 로그인 및 회원가입을 시도")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = User.class)))
    @PostMapping("/kakao/login")
    public ResponseEntity<ApiResponse<String>> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        String jwtToken = kakaoAuthService.loginWithKakaoToken(request.getAccessToken());
        return ApiResponse.success("token", jwtToken);
    }
}
