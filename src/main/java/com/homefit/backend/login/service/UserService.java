package com.homefit.backend.login.service;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        log.info("사용자 조회 시작: {}", userName);
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    log.error("사용자 조회 실패: 존재하지 않는 사용자명 - {}", userName);
                    return new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });
        log.info("사용자 조회 완료: ID={}, 이름={}", user.getId(), user.getUserName());
        return user;
    }
}
