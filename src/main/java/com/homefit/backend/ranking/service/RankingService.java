package com.homefit.backend.ranking.service;

import com.homefit.backend.ranking.dto.RankingUpdateRequestDto;
import com.homefit.backend.ranking.repository.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    public void updateUserExerciseRanking(RankingUpdateRequestDto requestDto) {
        rankingRepository.addUserScore(requestDto.getUserId(), requestDto.getCount());
    }

    public Set<Object> getTopUsers(int count) {
        return rankingRepository.getTopUsers(count);
    }

    public Long getUserRank(Long userId) {
        return rankingRepository.getUserRank(userId);
    }

    public Double getUserScore(Long userId) {
        return rankingRepository.getUserScore(userId);
    }
}
