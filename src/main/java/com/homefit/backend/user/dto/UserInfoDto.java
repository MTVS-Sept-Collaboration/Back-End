package com.homefit.backend.user.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String nickName;
    private LocalDate birthday;
    private Double height;
    private Double weight;
    private Double bmi;
}
