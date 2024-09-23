package com.homefit.backend.ranking.service;

import com.homefit.backend.ranking.dto.RankingUpdateRequestDto;
import com.homefit.backend.ranking.repository.RankingRepository;
import com.homefit.backend.ranking.util.RankingKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;

    // 일간, 주간, 월간 별로 유저의 운동 기록을 각각의 key에 맞춰 업데이트
    public void updateUserExerciseRanking(RankingUpdateRequestDto requestDto) {

        String dailyKey = RankingKeyUtil.getDailyRankingKey();
        String weeklyKey = RankingKeyUtil.getWeeklyRankingKey();
        String monthlyKey = RankingKeyUtil.getMonthlyRankingKey();

        rankingRepository.addUserScore(requestDto.getUserId(), requestDto.getCount(), dailyKey);
        rankingRepository.addUserScore(requestDto.getUserId(), requestDto.getCount(), weeklyKey);
        rankingRepository.addUserScore(requestDto.getUserId(), requestDto.getCount(), monthlyKey);
    }

    // 특정 기간의 상위 유저 조회
    public Set<Object> getTopUsers(String period, int count) {
        String rankingKey = getRankingKeyByPeriod(period);
        return rankingRepository.getTopUsers(rankingKey, count);
    }

    // 특정 기간의 유저 랭킹 조회
    public Long getUserRank(String period, Long userId) {
        String rankingKey = getRankingKeyByPeriod(period);
        return rankingRepository.getUserRank(rankingKey, userId);
    }

    // 특정 기간의 유저 점수 조회
    public Double getUserScore(String period, Long userId) {
        String rankingKey = getRankingKeyByPeriod(period);
        return rankingRepository.getUserScore(rankingKey, userId);
    }

    // 기간에 맞는 key 선택
    private String getRankingKeyByPeriod(String period) {
        
        switch (period) {
            case "daily":
                return RankingKeyUtil.getDailyRankingKey();
            case "weekly":
                return RankingKeyUtil.getWeeklyRankingKey();
            case "monthly":
                return RankingKeyUtil.getMonthlyRankingKey();
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }
}
