package com.homefit.backend.ranking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankingRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    // 유저의 운동 카운트를 해당 key에 맞춰 추가 (일간, 주간, 월간)
    public void addUserScore(Long userId, Long exerciseCount, String rankingKey) {
        redisTemplate.opsForZSet().incrementScore(rankingKey, String.valueOf(userId), exerciseCount);
    }

    // 특정 key의 상위 n명의 유저를 가져옴
    public Set<Object> getTopUsers(String rankingKey, int count) {
        return redisTemplate.opsForZSet().reverseRange(rankingKey, 0, count - 1);
    }

    // 특정 key에서 유저의 랭킹을 가져옴
    public Long getUserRank(String rankingKey, Long userId) {
        return redisTemplate.opsForZSet().reverseRank(rankingKey, String.valueOf(userId));
    }

    // 특정 key에서 유저의 점수를 가져옴
    public Double getUserScore(String rankingKey, Long userId) {
        return redisTemplate.opsForZSet().score(rankingKey, String.valueOf(userId));
    }
}
