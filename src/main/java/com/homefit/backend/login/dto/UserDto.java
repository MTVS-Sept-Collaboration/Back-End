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
public class UserDto {
    private Long id;
    private String kakaoId;
    private String nickName;
    private LocalDate birthday;
    private String profileImage;
    private RoleType role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate firedAt;
    private Boolean userStatus;
    private String refreshToken;
}
