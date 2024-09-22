package com.homefit.backend.ranking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankingRepository {

    private static final String RANKING_KEY = "user_ranking";

    // String key, Object value로 설정된 RedisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    // 유저의 운동 카운트
    public void addUserScore(Long userId, Long exerciseCount) {
        // userId를 String으로 변환하여 Redis에 저장
        redisTemplate.opsForZSet().incrementScore(RANKING_KEY, String.valueOf(userId), exerciseCount);
    }

    // 탑 n개 유저의 기록을 가져옴
    public Set<Object> getTopUsers(int count) {
        return redisTemplate.opsForZSet().reverseRange(RANKING_KEY, 0, count - 1);
    }

    // 특정 유저의 랭킹을 가져옴
    public Long getUserRank(Long userId) {
        return redisTemplate.opsForZSet().reverseRank(RANKING_KEY, String.valueOf(userId));
    }

    // 특정 유저의 스코어를 가져옴
    public Double getUserScore(Long userId) {
        return redisTemplate.opsForZSet().score(RANKING_KEY, String.valueOf(userId));
    }
}
