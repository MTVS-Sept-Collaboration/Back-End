package com.homefit.backend.ranking.service;

import com.homefit.backend.ranking.dto.RankingUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RankingServiceTest {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String rankingKey = "user_ranking";

    @BeforeEach
    public void setUp() {
        // Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("운동 랭킹 업데이트")
    public void testUpdateUserExerciseRanking() {
        // 유저의 운동 횟수를 업데이트
        RankingUpdateRequestDto requestDto = new RankingUpdateRequestDto(1L, 10L);
        rankingService.updateUserExerciseRanking(requestDto);

        // ZSET에 유저의 점수가 저장되었는지 확인
        assertEquals(10.0, redisTemplate.opsForZSet().score(rankingKey, "1"));
    }

    @Test
    @DisplayName("Top N명의 운동 기록 가져오기")
    public void testGetTopUsers() {
        // 유저의 운동 횟수를 업데이트
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(1L, 10L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(2L, 20L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(3L, 15L));

        // 상위 2명의 유저를 가져옴
        Set<Object> topUsers = rankingService.getTopUsers(2);

        assertEquals(2, topUsers.size());
        assertTrue(topUsers.contains("2"));  // 2번 유저는 1등 (운동량 20)
        assertTrue(topUsers.contains("3"));  // 3번 유저는 2등 (운동량 15)
    }

    @Test
    @DisplayName("특정 유저의 랭킹 가져오기")
    public void testGetUserRank() {
        // 유저의 운동 횟수를 업데이트
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(1L, 10L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(2L, 20L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(3L, 15L));

        // 각 유저의 랭킹을 확인
        Long rank1 = rankingService.getUserRank(2L);
        Long rank2 = rankingService.getUserRank(3L);
        Long rank3 = rankingService.getUserRank(1L);

        assertEquals(0, rank1);  // 2번 유저가 1등
        assertEquals(1, rank2);  // 3번 유저가 2등
        assertEquals(2, rank3);  // 1번 유저가 3등
    }

    @Test
    @DisplayName("특정 유저의 운동 스코어 가져오기")
    public void testGetUserScore() {
        Long userId = 1L;
        Long exerciseCount = 10L;

        // 유저의 운동 횟수를 업데이트
        RankingUpdateRequestDto requestDto = new RankingUpdateRequestDto(userId, exerciseCount);
        rankingService.updateUserExerciseRanking(requestDto);

        // 유저의 점수를 확인
        Double score = rankingService.getUserScore(userId);
        assertEquals(10.0, score);
    }
}
