package com.homefit.backend.character.controller;

import com.homefit.backend.character.dto.CharacterUpdateRequestDto;
import com.homefit.backend.character.service.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/character")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @Operation(summary = "유저 캐릭터 저장", description = "유저의 캐릭터 상태를 저장합니다. user의 ID를 기반으로 유저를 검색합니다.")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateCharacter(@PathVariable Long userId, @RequestBody CharacterUpdateRequestDto requestDto) {
        characterService.updateCharacter(userId, requestDto);
        return ResponseEntity.ok().build();
    }
}
