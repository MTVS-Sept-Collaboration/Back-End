package com.homefit.backend.login.controller;

import com.homefit.backend.login.dto.LoginDto;
import com.homefit.backend.login.dto.PasswordChangeDto;
import com.homefit.backend.login.dto.UserDto;
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
        log.info("Registering new user with username: {} and role: {}", userDto.getUserName(), userDto.getRole());
        try {
            userService.registerUser(userDto);
            log.info("User registered successfully: {}", userDto.getUserName());
            return ResponseEntity.ok("회원가입에 성공했어요!");
        } catch (Exception e) {
            log.error("Error registering user: {}", userDto.getUserName(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "로그인", description = "사용자 또는 관리자 로그인을 위한 엔드포인트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        log.info("Attempting login for user: {}", loginDto.getUserName());
        try {
            String token = authenticationService.login(loginDto);
            log.info("Login successful for user: {}", loginDto.getUserName());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.getUserName(), e);
            return ResponseEntity.badRequest().body("아이디 혹은 비밀번호가 일치하지 않아요...");
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
        log.info("Attempting to change password for user: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            userService.changePassword(passwordChangeDto);
            log.info("Password changed successfully for user: {}", SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었어요!");
        } catch (Exception e) {
            log.error("Failed to change password for user: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
