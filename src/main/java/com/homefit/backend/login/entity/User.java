package com.homefit.backend.login.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginTime; // 로그인 시각
    private LocalDateTime lastLogoutTime; // 로그아웃 시각

    @Builder
    public User(Long id, String userName, String password, RoleType role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public void updateLogoutTime() {
        this.lastLogoutTime = LocalDateTime.now();
    }
}