package com.homefit.backend.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    @Schema(description = "사용자 아이디")
    private Long userId;

    @Schema(description = "JWT")
    private String jwtToken;
}
