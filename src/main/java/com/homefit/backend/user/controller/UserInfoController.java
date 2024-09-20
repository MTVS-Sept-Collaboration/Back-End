package com.homefit.backend.user.controller;

import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.service.UserInfoService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 정보", description = "사용자 정보 API")
@SecurityRequirement(name = "bearerAuth")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "사용자 닉네임 수정", description = "사용자의 닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "닉네임 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}/nickname")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateNickName(
            @PathVariable(value = "id") Long userId,
            @RequestBody String nickName
    ) {
        log.info("사용자 닉네임 수정 요청: 사용자 ID = {}", userId);
        try {
            // 쌍따옴표 제거
            nickName = nickName.replaceAll("^\"|\"$", "");
            userInfoService.updateNickName(userId, nickName);
            log.info("사용자 닉네임 수정 성공: 사용자 ID = {}", userId);
            return ResponseEntity.ok("사용자의 닉네임이 성공적으로 변경되었어요!");
        } catch (Exception e) {
            log.error("사용자 닉네임 수정 실패: 사용자 ID = {}", userId, e);
            return ResponseEntity.badRequest().body("닉네임 변경 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 생년월일 수정", description = "사용자의 생년월일을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "생년월일 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}/birthday")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateBirthday(
            @PathVariable(value = "id") Long userId,
            @RequestBody LocalDate birthday
    ) {
        log.info("사용자 생년월일 수정 요청: 사용자 ID = {}", userId);
        try {
            userInfoService.updateBirthday(userId, birthday);
            log.info("사용자 생년월일 수정 성공: 사용자 ID = {}", userId);
            return ResponseEntity.ok("사용자의 생년월일이 성공적으로 변경되었어요!");
        } catch (Exception e) {
            log.error("사용자 생년월일 수정 실패: 사용자 ID = {}", userId, e);
            return ResponseEntity.badRequest().body("생년월일 변경 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 신체 정보 수정", description = "사용자의 키와 몸무게 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "신체 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}/body")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserPhysicalInfo(
            @PathVariable(value = "id") Long userId,
            @RequestBody UserPhysicalInfoDto userPhysicalInfoDto
    ) {
        log.info("사용자 신체 정보 수정 요청: 사용자 ID = {}", userId);
        try {
            userInfoService.updateUserPhysicalInfo(userId, userPhysicalInfoDto);
            log.info("사용자 신체 정보 수정 성공: 사용자 ID = {}", userId);
            return ResponseEntity.ok("사용자의 신체 정보가 성공적으로 변경되었어요!");
        } catch (Exception e) {
            log.error("사용자 신체 정보 수정 실패: 사용자 ID = {}", userId, e);
            return ResponseEntity.badRequest().body("신체 정보 변경 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 정보 일괄 수정", description = "사용자의 닉네임, 생년월일, 신체 정보를 일괄 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserInfo(
            @PathVariable(value = "id") Long userId,
            @RequestBody UserInfoDto userInfoDto
    ) {
        log.info("사용자 전체 정보 수정 요청: 사용자 ID = {}", userId);
        try {
            userInfoService.updateUserInfo(userId, userInfoDto);
            log.info("사용자 전체 정보 수정 성공: 사용자 ID = {}", userId);
            return ResponseEntity.ok("사용자의 모든 정보가 성공적으로 변경되었어요!");
        } catch (Exception e) {
            log.error("사용자 전체 정보 수정 실패: 사용자 ID = {}", userId, e);
            return ResponseEntity.badRequest().body("정보 변경 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoDto.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoDto> getUserInfo(
            @PathVariable(value = "id") Long userId
    ) {
        log.info("사용자 정보 조회 요청: 사용자 ID = {}", userId);
        try {
            UserInfoDto userInfoDto = userInfoService.getUserInfo(userId);
            log.info("사용자 정보 조회 성공: 사용자 ID = {}", userId);
            return ResponseEntity.ok(userInfoDto);
        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: 사용자 ID = {}", userId, e);
            return ResponseEntity.badRequest().body(null);
        }
    }
//    @GetMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<UserInfoDto> getUserInfo(
//            @PathVariable(value = "id") Long userId
//    ) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.debug("Current authentication: {}", authentication);
//
//        UserInfoDto userInfoDto = userInfoService.getUserInfo(userId);
//        return ResponseEntity.ok(userInfoDto);
//    }
}
