package com.homefit.backend.login.dto;

import com.homefit.backend.login.oauth.entity.RoleType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {
    private Long id;
    private String userName; // 닉네임
    private String email; // 이메일
    private String nickName; // 닉네임
    private String profileImage;
    private RoleType role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate firedAt;
    private Boolean userStatus;
    private String refreshToken;
}
