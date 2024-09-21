package com.homefit.backend.login.controller;

import com.homefit.backend.login.dto.LoginRequestDto;
import com.homefit.backend.login.dto.LoginResponseDto;
import com.homefit.backend.login.dto.PasswordChangeDto;
import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.service.AuthenticationService;
import com.homefit.backend.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Tag(name = "로그인/회원가입 API", description = "로그인 및 회원가입 API")
public class UserAuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "사용자 계정 생성", description = "사용자 계정을 생성하기 위한 엔드포인트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "사용자 계정 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        log.info("회원가입 시도 사용자명: {} & 역할: {}", userDto.getUserName(), userDto.getRole());
        try {
            User registeredUser = userService.registerUser(userDto);
            log.info("사용자 회원가입 성공: {}", userDto.getUserName());
            return ResponseEntity.ok("회원가입에 성공했습니다. 사용자 ID: " + registeredUser.getId());
        } catch (Exception e) {
            log.error("사용자 회원가입 오류 발생: {}", userDto.getUserName(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "사용자 또는 관리자 로그인을 위한 엔드포인트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("로그인 시도: {}", loginRequestDto.getUserName());
        try {
            LoginResponseDto response = authenticationService.login(loginRequestDto);
            log.info("로그인 성공: {}", loginRequestDto.getUserName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 실패: {}", loginRequestDto.getUserName(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "비밀번호 변경", description = "사용자 또는 관리자의 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeDto) {
        log.info("비밀번호 변경 시도: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            userService.changePassword(passwordChangeDto);
            log.info("비밀번호 변경 성공: {}", SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("비밀번호 변경 실패: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
