package com.homefit.backend.character.controller;

import com.homefit.backend.character.dto.CharacterUpdateRequestDto;
import com.homefit.backend.character.service.CharacterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/api/character")
@RequiredArgsConstructor
@Tag(name = "사용자 캐릭터", description = "사용자 캐릭터 API")
@SecurityRequirement(name = "bearerAuth")
public class CharacterController {

    private final CharacterService characterService;

    @Operation(summary = "유저 캐릭터 업데이트", description = "유저의 캐릭터 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "유저 캐릭터 수정 성공",
                    content = @Content(schema = @Schema(implementation = CharacterUpdateRequestDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateCharacter(@PathVariable Long userId, @RequestBody CharacterUpdateRequestDto requestDto) {
        log.info("캐릭터 업데이트 요청: 사용자 ID = {}", userId);
        Long updatedUserId = characterService.updateCharacter(userId, requestDto);
        log.info("캐릭터 업데이트 완료: 사용자 ID = {}", updatedUserId);
        return ResponseEntity.ok(updatedUserId);
    }

    @Operation(summary = "유저 캐릭터 조회", description = "유저의 캐릭터 상태를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 캐릭터 조회 성공",
                    content = @Content(schema = @Schema(implementation = CharacterUpdateRequestDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CharacterUpdateRequestDto> getCharacter(@PathVariable Long userId) {
        log.info("캐릭터 조회 요청: 사용자 ID = {}", userId);
        CharacterUpdateRequestDto character = characterService.getCharacter(userId);
        log.info("캐릭터 조회 완료: 사용자 ID = {}", userId);
        return ResponseEntity.ok(character);
    }
}
