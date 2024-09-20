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
            log.error("사용자 등록 실패: 이미 존재하는 사용자명입니다. (사용자명: {})", userDto.getUserName());
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        User user = User.builder()
                .userName(userDto.getUserName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole() != null ? userDto.getRole() : RoleType.USER)
                .build();

        user = userRepository.save(user);
        log.info("새로운 사용자가 등록되었습니다. (사용자명: {}, 역할: {})", user.getUserName(), user.getRole());

        UserInfo userInfo = new UserInfo(null, user, user.getUserName(), null, null, null);
        userInfoRepository.save(userInfo);
        log.info("사용자 정보가 생성되었습니다. (사용자명: {})", user.getUserName());

        return user;
    }

    @Transactional
    public User registerInitialAdminUser(AdminDto adminDto) {
        if (userRepository.existsByRole(RoleType.ADMIN)) {
            log.info("이미 초기 관리자 계정이 존재합니다. 생성을 생략합니다.");
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
            log.error("관리자 등록 실패: 이미 존재하는 관리자명입니다. (괸리자명: {})", adminDto.getUserName());
            throw new RuntimeException("이미 존재하는 관리자명입니다.");
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
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        String newEncodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        user.changePassword(newEncodedPassword);
        userRepository.save(user);
    }
}
