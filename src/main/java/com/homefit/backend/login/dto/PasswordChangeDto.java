package com.homefit.backend.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDto {
    @Schema(description = "기존 비밀번호")
    private String currentPassword;

    @Schema(description = "새로운 비밀번호")
    private String newPassword;
}
