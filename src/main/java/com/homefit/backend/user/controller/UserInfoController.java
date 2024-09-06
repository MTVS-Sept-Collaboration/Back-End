package com.homefit.backend.user.controller;

import com.homefit.backend.login.common.CustomApiResponse;
import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Info")
@SecurityRequirement(name = "bearerAuth")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "사용자 생년월일 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "생년월일 수정 성공",
                    content = @Content(schema = @Schema(implementation = CustomApiResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}/birthday")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomApiResponse<Void>> updateBirthday(
            @PathVariable(value = "id") Long userId,
            @RequestBody LocalDate birthday
    ) {
        userInfoService.updateBirthday(userId, birthday);
        return CustomApiResponse.success("# 생년월일이 성공적으로 업데이트 되었어요!", null);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자의 키와 몸무게 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = CustomApiResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomApiResponse<Void>> updateUserInfo(
            @PathVariable(value = "id") Long userId,
            @RequestBody UserPhysicalInfoDto userPhysicalInfoDto
    ) {
        userInfoService.updateUserPhysicalInfo(userId, userPhysicalInfoDto);
        return CustomApiResponse.success("# 사용자 정보가 성공적으로 업데이트 되었어요!", null);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomApiResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CustomApiResponse<UserInfoDto>> getUserInfo(
            @PathVariable(value = "id") Long userId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Current authentication: {}", authentication);

        UserInfoDto userInfoDto = userInfoService.getUserInfo(userId);
        return CustomApiResponse.success("# 사용자 정보가 정상적으로 조회되었어요!", userInfoDto);
    }
}
