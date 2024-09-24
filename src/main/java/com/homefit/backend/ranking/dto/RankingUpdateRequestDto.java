package com.homefit.backend.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingUpdateRequestDto {

    private Long userId;
    private Long count;
}
