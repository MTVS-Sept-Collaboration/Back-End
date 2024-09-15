package com.homefit.backend.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    @Schema(description = "아이디")
    private String userName;

    @Schema(description = "비밀번호")
    private String password;
}