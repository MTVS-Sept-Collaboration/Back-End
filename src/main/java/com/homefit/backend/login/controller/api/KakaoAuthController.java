package com.homefit.backend.login.controller.api;

import com.homefit.backend.login.common.CustomApiResponse;
import com.homefit.backend.login.dto.KakaoLoginRequest;
import com.homefit.backend.login.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "카카오 로그인/회원가입", description = "카카오 인증 관련 API")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(summary = "발급받은 액세스 토큰으로 로그인 및 회원가입을 시도")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PostMapping("/kakao/login")
    public ResponseEntity<CustomApiResponse<String>> kakaoLogin(
            @RequestBody KakaoLoginRequest request
    ) {
        String jwtToken = kakaoAuthService.loginWithKakaoToken(request.getAccessToken());
        return CustomApiResponse.success("token", jwtToken);
    }
}
