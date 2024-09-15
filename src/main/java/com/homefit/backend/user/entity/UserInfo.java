package com.homefit.backend.user.entity;

import com.homefit.backend.login.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "user_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;
    private LocalDate birthday;
    private Double height;
    private Double weight;
    private Double bmi;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserInfo(Long id, User user, String nickName, LocalDate birthday, Double height, Double weight) {
        this.id = id;
        this.user = user;
        this.nickName = nickName;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.calculateAndSetBmi();
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void updatePhysicalInfo(Double height, Double weight) {
        if (height != null) {
            this.height = height;
        }
        if (weight != null) {
            this.weight = weight;
        }
        calculateAndSetBmi();
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
