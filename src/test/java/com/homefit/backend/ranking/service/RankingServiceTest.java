package com.homefit.backend.ranking.service;

import com.homefit.backend.ranking.dto.RankingUpdateRequestDto;
import com.homefit.backend.ranking.util.RankingKeyUtil;
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

    @BeforeEach
    public void setUp() {
        // Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    @DisplayName("유저 랭킹 기록 테스트")
    public void testUpdateUserExerciseRanking() {

        // 유저 1의 운동 횟수를 업데이트 (일간, 주간, 월간 모두)
        RankingUpdateRequestDto requestDto = new RankingUpdateRequestDto(1L, 10L);
        rankingService.updateUserExerciseRanking(requestDto);

        // ZSET에 유저의 점수가 저장되었는지 확인 (일간, 주간, 월간 모두)
        assertNotNull(redisTemplate.opsForZSet().score(RankingKeyUtil.getDailyRankingKey(), "1"));
        assertNotNull(redisTemplate.opsForZSet().score(RankingKeyUtil.getWeeklyRankingKey(), "1"));
        assertNotNull(redisTemplate.opsForZSet().score(RankingKeyUtil.getMonthlyRankingKey(), "1"));
    }

    @Test
    @DisplayName("Top N명의 유저 랭킹 조회 테스트")
    public void testGetTopUsers() {

        // 유저들의 운동 횟수를 업데이트
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(1L, 10L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(2L, 20L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(3L, 15L));

        // 일간 상위 2명의 유저를 가져옴
        Set<Object> dailyTopUsers = rankingService.getTopUsers("daily", 2);
        assertEquals(2, dailyTopUsers.size());
        assertTrue(dailyTopUsers.contains("2"));  // 2번 유저는 1등 (운동량 20)
        assertTrue(dailyTopUsers.contains("3"));  // 3번 유저는 2등 (운동량 15)

        // 주간 상위 2명의 유저를 가져옴
        Set<Object> weeklyTopUsers = rankingService.getTopUsers("weekly", 2);
        assertEquals(2, weeklyTopUsers.size());
        assertTrue(weeklyTopUsers.contains("2"));
        assertTrue(weeklyTopUsers.contains("3"));

        // 월간 상위 2명의 유저를 가져옴
        Set<Object> monthlyTopUsers = rankingService.getTopUsers("monthly", 2);
        assertEquals(2, monthlyTopUsers.size());
        assertTrue(monthlyTopUsers.contains("2"));
        assertTrue(monthlyTopUsers.contains("3"));
    }

    @Test
    @DisplayName("Top N명 유저의 등수 조회 테스트")
    public void testGetUserRank() {

        // 유저들의 운동 횟수를 업데이트
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(1L, 10L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(2L, 20L));
        rankingService.updateUserExerciseRanking(new RankingUpdateRequestDto(3L, 15L));

        // 일간 랭킹 확인
        Long dailyRank1 = rankingService.getUserRank("daily", 2L);
        Long dailyRank2 = rankingService.getUserRank("daily", 3L);
        Long dailyRank3 = rankingService.getUserRank("daily", 1L);

        assertEquals(0, dailyRank1);  // 2번 유저가 1등
        assertEquals(1, dailyRank2);  // 3번 유저가 2등
        assertEquals(2, dailyRank3);  // 1번 유저가 3등

        // 주간 랭킹 확인
        Long weeklyRank1 = rankingService.getUserRank("weekly", 2L);
        Long weeklyRank2 = rankingService.getUserRank("weekly", 3L);
        Long weeklyRank3 = rankingService.getUserRank("weekly", 1L);

        assertEquals(0, weeklyRank1);
        assertEquals(1, weeklyRank2);
        assertEquals(2, weeklyRank3);

        // 월간 랭킹 확인
        Long monthlyRank1 = rankingService.getUserRank("monthly", 2L);
        Long monthlyRank2 = rankingService.getUserRank("monthly", 3L);
        Long monthlyRank3 = rankingService.getUserRank("monthly", 1L);

        assertEquals(0, monthlyRank1);
        assertEquals(1, monthlyRank2);
        assertEquals(2, monthlyRank3);
    }

    @Test
    @DisplayName("Top N명 유저의 운동 카운트 조회 테스트")
    public void testGetUserScore() {

        Long userId = 1L;
        Long exerciseCount = 10L;

        // 유저의 운동 횟수를 업데이트 (일간, 주간, 월간 모두)
        RankingUpdateRequestDto requestDto = new RankingUpdateRequestDto(userId, exerciseCount);
        rankingService.updateUserExerciseRanking(requestDto);

        // 유저의 점수를 확인 (일간)
        Double dailyScore = rankingService.getUserScore("daily", userId);
        assertEquals(10.0, dailyScore);

        // 유저의 점수를 확인 (주간)
        Double weeklyScore = rankingService.getUserScore("weekly", userId);
        assertEquals(10.0, weeklyScore);

        // 유저의 점수를 확인 (월간)
        Double monthlyScore = rankingService.getUserScore("monthly", userId);
        assertEquals(10.0, monthlyScore);
    }
}
