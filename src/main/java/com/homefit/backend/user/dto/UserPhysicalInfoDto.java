package com.homefit.backend.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhysicalInfoDto {
    private Double height;
    private Double weight;
}
