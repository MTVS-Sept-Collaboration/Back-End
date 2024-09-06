package com.homefit.backend.user.entity;

import com.homefit.backend.login.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double height;

    private Double weight;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserInfo(Long id, Double height, Double weight, User user) {
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.user = user;
    }

    public UserInfo updateInfo(Double height, Double weight) {
        return UserInfo.builder()
                .id(this.id)
                .height(height)
                .weight(weight)
                .user(this.user)
                .build();
    }
}
