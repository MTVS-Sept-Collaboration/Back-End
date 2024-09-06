package com.homefit.backend.login.entity;

import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.user.entity.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String kakaoId;

    private String nickName;

    // 생년월일 필드 추가
    private LocalDate birthday;

    @Column(length = 512)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate firedAt;

    private Boolean userStatus;

    private String refreshToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserInfo userInfo;

    @Builder
    public User(Long id, String kakaoId, String nickName, LocalDate birthday,
                String profileImage, RoleType role, LocalDateTime createdAt,
                LocalDateTime updatedAt, LocalDate firedAt, Boolean userStatus,
                String refreshToken) {
        this.id = id;
        this.kakaoId = kakaoId;
        this.nickName = nickName;
        this.birthday = birthday;
        this.profileImage = profileImage;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firedAt = firedAt;
        this.userStatus = userStatus;
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String nickName, String profileImage) {
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}