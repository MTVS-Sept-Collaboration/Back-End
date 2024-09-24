package com.homefit.backend.login.service;

import com.homefit.backend.character.service.CharacterService;
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
    private final CharacterService characterService;

    @Transactional
    public User registerUser(UserDto userDto) {
        log.info("사용자 등록 시도: 사용자명 = {}", userDto.getUserName());

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
        log.info("새로운 사용자가 등록되었습니다. (사용자 ID: {}, 사용자명: {}, 역할: {})", user.getId(), user.getUserName(), user.getRole());

        try {
            characterService.createCharacterForUser(user);
            log.info("사용자의 캐릭터가 생성되었습니다. (사용자 ID: {})", user.getId());
        } catch (Exception e) {
            log.error("사용자 캐릭터 생성 중 오류 발생: (사용자 ID: {})", user.getId(), e);
            throw new RuntimeException("캐릭터 생성 중 오류가 발생했습니다.", e);
        }

        try {
            UserInfo userInfo = new UserInfo(null, user, user.getUserName(), null, null, null);
            userInfoRepository.save(userInfo);
            log.info("사용자 정보가 생성되었습니다. (사용자 ID: {}, 사용자명: {})", user.getId(), user.getUserName());
        } catch (Exception e) {
            log.error("사용자 정보 생성 중 오류 발생: (사용자 ID: {})", user.getId(), e);
            throw new RuntimeException("사용자 정보 생성 중 오류가 발생했습니다.", e);
        }

        log.info("사용자 등록 프로세스 완료: (사용자 ID: {}, 사용자명: {})", user.getId(), user.getUserName());
        return user;
    }

    @Transactional
    public User registerInitialAdminUser(AdminDto adminDto) {
        log.info("초기 관리자 계정 생성 시도");
        if (userRepository.existsByRole(RoleType.ADMIN)) {
            log.info("이미 초기 관리자 계정이 존재합니다. 생성을 생략합니다.");
            return null;
        }

        User user = User.builder()
                .userName(adminDto.getUserName())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .role(RoleType.ADMIN)
                .build();

        user = userRepository.save(user);
        log.info("초기 관리자 계정이 생성되었습니다. (관리자 ID: {}, 사용자명: {})", user.getId(), user.getUserName());
        return user;
    }

    @Transactional
    public User registerAdminUser(AdminDto adminDto) {
        log.info("관리자 계정 등록 시도: 사용자명 = {}", adminDto.getUserName());
        if (userRepository.existsByUserName(adminDto.getUserName())) {
            log.error("관리자 등록 실패: 이미 존재하는 관리자명입니다. (관리자명: {})", adminDto.getUserName());
            throw new RuntimeException("이미 존재하는 관리자명입니다.");
        }

        User user = User.builder()
                .userName(adminDto.getUserName())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .role(adminDto.getRole() != null ? adminDto.getRole() : RoleType.ADMIN)
                .build();

        user = userRepository.save(user);
        log.info("새로운 관리자가 등록되었습니다. (관리자 ID: {}, 사용자명: {}, 역할: {})", user.getId(), user.getUserName(), user.getRole());
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        log.info("사용자 조회 시도: 사용자명 = {}", userName);
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: 사용자명 = {}", userName);
                    return new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });
        log.info("사용자 조회 성공: 사용자 ID = {}, 사용자명 = {}", user.getId(), user.getUserName());
        return user;
    }

    @Transactional
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        log.info("비밀번호 변경 시도");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("비밀번호 변경 실패: 사용자를 찾을 수 없음 (사용자명: {})", username);
                    return new RuntimeException("해당 사용자를 찾을 수 없습니다.");
                });

        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            log.error("비밀번호 변경 실패: 현재 비밀번호가 일치하지 않음 (사용자 ID: {})", user.getId());
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        String newEncodedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        user.changePassword(newEncodedPassword);
        userRepository.save(user);
        log.info("비밀번호 변경 성공 (사용자 ID: {})", user.getId());
    }
}
