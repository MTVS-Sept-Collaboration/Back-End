package com.homefit.backend.user.service;

import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final AuthTokenProvider tokenProvider;

    /** 사용자의 생년월일 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateBirthday(Long userId, LocalDate birthday) {
        User user = getUserById(userId);
        User updatedUser = User.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .nickName(user.getNickName())
                .birthday(birthday)
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .firedAt(user.getFiredAt())
                .userStatus(user.getUserStatus())
                .refreshToken(user.getRefreshToken())
                .build();
        userRepository.save(updatedUser);
    }

    /** 사용자의 키와 몸무게 정보만을 수정하기 위한 메서드 */
    @Transactional
    public void updateUserPhysicalInfo(Long userId, UserPhysicalInfoDto userPhysicalInfoDto) {
        User user = getUserById(userId);
        UserInfo existingUserInfo = userInfoRepository.findByUser(user)
                .orElse(null);

        UserInfo updatedUserInfo = UserInfo.builder()
                .id(existingUserInfo != null ? existingUserInfo.getId() : null)
                .height(userPhysicalInfoDto.getHeight())
                .weight(userPhysicalInfoDto.getWeight())
                .user(user)
                .build();

        userInfoRepository.save(updatedUserInfo);
    }

    public UserInfoDto getUserInfo(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Authentication required");
        }

        String currentUserId = authentication.getName();
        log.debug("Current user ID: {}, Requested user ID: {}", currentUserId, userId);
        if (!currentUserId.equals(userId.toString())) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        User user = getUserById(userId);
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(UserInfo.builder().user(user).build());

        return convertToDto(userInfo);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserInfoDto convertToDto(UserInfo userInfo) {
        return UserInfoDto.builder()
                .userId(userInfo.getUser().getId()) // User 엔티티에서 ID를 가져옴
                .birthday(userInfo.getUser().getBirthday())
                .height(userInfo.getHeight())
                .weight(userInfo.getWeight())
                .build();
    }
}
