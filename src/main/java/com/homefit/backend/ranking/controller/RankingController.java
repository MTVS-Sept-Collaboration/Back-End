package com.homefit.backend.ranking.controller;

import com.homefit.backend.ranking.dto.RankingUpdateRequestDto;
import com.homefit.backend.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    @PostMapping("/update")
    public void updateUserExercise(@RequestBody RankingUpdateRequestDto requestDto) {
        rankingService.updateUserExerciseRanking(requestDto);
    }

    @GetMapping("/top")
    public Set<Object> getTopUsers(@RequestParam int count) {
        return rankingService.getTopUsers(count);
    }

    @GetMapping("/rank")
    public Long getUserRank(@RequestParam Long userId) {
        return rankingService.getUserRank(userId);
    }

    @GetMapping("/score")
    public Double getUserScore(@RequestParam Long userId) {
        return rankingService.getUserScore(userId);
    }
}
