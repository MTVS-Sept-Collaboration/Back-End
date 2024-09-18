package com.homefit.backend.login.config;

import com.homefit.backend.login.dto.AdminDto;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            AdminDto adminDto = new AdminDto();
            adminDto.setUserName("admin");
            adminDto.setPassword("admin");
            adminDto.setRole(RoleType.ADMIN);

            userService.registerInitialAdminUser(adminDto);
            log.info("Initial admin user created successfully");
        } catch (Exception e) {
            log.error("Failed to create initial admin user", e);
        }
    }
}
