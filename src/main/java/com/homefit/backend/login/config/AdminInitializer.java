package com.homefit.backend.login.config;

import com.homefit.backend.login.dto.AdminDto;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final AuthService authService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            AdminDto adminDto = new AdminDto();
            adminDto.setUserName("admin");
            adminDto.setPassword("admin");
            adminDto.setRole(RoleType.ADMIN);

            authService.registerInitialAdminUser(adminDto);
            log.info("초기 관리자 계정 생성 성공");
        } catch (Exception e) {
            log.error("초기 관리자 계정 생성 실패", e);
        }
    }
}
