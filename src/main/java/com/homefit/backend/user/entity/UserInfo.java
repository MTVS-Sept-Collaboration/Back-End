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
    private String nickname;
    private Double height;
    private Double weight;
    private Double bmi;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserInfo(Long id, User user, String nickname, Double height, Double weight) {
        this.id = id;
        this.user = user;
        this.nickname = nickname;
        this.height = height;
        this.weight = weight;
        this.calculateAndSetBmi();
    }

    public void updateInfo(Double height, Double weight) {
        this.height = height;
        this.weight = weight;
        this.calculateAndSetBmi();
    }

    private void calculateAndSetBmi() {
        if (this.height != null && this.weight != null && this.height > 0) {
            double heightInMeters = this.height / 100.0;
            this.bmi = this.weight / (heightInMeters * heightInMeters);
        } else {
            this.bmi = null;
        }
    }
}
