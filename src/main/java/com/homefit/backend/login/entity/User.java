package com.homefit.backend.login.entity;

import com.homefit.backend.login.oauth.entity.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String nickName;
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate firedAt;
    private Boolean userStatus;
    private String refreshToken;

    public User(String userName, String email, String nickName, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDate firedAt, Boolean userStatus, String refreshToken) {
        this.userName = userName;
        this.email = email;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firedAt = firedAt;
        this.userStatus = userStatus;
        this.refreshToken = refreshToken;
    }
}