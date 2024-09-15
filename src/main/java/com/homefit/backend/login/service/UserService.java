package com.homefit.backend.login.service;

import com.homefit.backend.login.dto.AdminDto;
import com.homefit.backend.login.dto.PasswordChangeDto;
import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserDto userDto) {
        if (userRepository.existsByUserName(userDto.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .userName(userDto.getUserName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole() != null ? userDto.getRole() : RoleType.USER)  // 기본값으로 USER 역할 설정
                .build();

        user = userRepository.save(user);

        UserInfo userInfo = new UserInfo(null, user, user.getUserName(), null, null, null);
        userInfoRepository.save(userInfo);

        return user;
    }

    @Transactional
    public User registerInitialAdminUser(AdminDto adminDto) {
        if (userRepository.existsByRole(RoleType.ADMIN)) {
            log.info("Admin user already exists. Skipping initial admin creation.");
            return null;
        }

        User user = User.builder()
                .userName(adminDto.getUserName())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .role(RoleType.ADMIN)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User registerAdminUser(AdminDto adminDto) {
        if (userRepository.existsByUserName(adminDto.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .userName(adminDto.getUserName())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .role(adminDto.getRole() != null ? adminDto.getRole() : RoleType.ADMIN)  // 기본값으로 ADMIN 역할 설정
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        String newEncodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        user.changePassword(newEncodedPassword);
        userRepository.save(user);
    }
}
