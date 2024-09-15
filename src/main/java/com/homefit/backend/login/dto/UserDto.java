package com.homefit.backend.login.dto;

import com.homefit.backend.login.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
public class UserDto {
    @Schema(description = "사용자 아이디")
    private String userName;

    @Schema(description = "사용자 비밀번호")
    private String password;

    @Schema(description = "권한(USER: 사용자, ADMIN: 관리자)", defaultValue = "USER")
    private RoleType role;
}
