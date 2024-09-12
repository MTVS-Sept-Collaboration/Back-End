package com.homefit.backend.user.service;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

    @Setter
    private boolean testMode = false;

    /** 사용자의 닉네임 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        logger.info("Starting updateNickname for user: {}", userId);
        try {
            User user = getUserById(userId);
            logger.debug("Retrieved user: {}", user.getId());

            logger.info("Updating nickname for user: {} - Old value: {}", userId, user.getNickName());

            User updatedUser = User.builder()
                    .id(user.getId())
                    .kakaoId(user.getKakaoId())
                    .nickName(nickname)
                    .birthday(user.getBirthday())
                    .profileImage(user.getProfileImage())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .firedAt(user.getFiredAt())
                    .userStatus(user.getUserStatus())
                    .build();
            userRepository.save(updatedUser);
            logger.debug("Saved updated UserInfo to database");

            logger.info("Successfully updated nickname for user: {} - New value: {}", userId, nickname);
        } catch (Exception e) {
            logger.error("Error occurred while updating nickname info for user: {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 생년월일 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateBirthday(Long userId, LocalDate birthday) {
        logger.info("Starting updateBirthday for user: {}", userId);
        try {
            User user = getUserById(userId);
            logger.debug("Retrieved user: {}", user.getId());

            logger.info("Updating birthday for user: {} - Old value: {}", userId, user.getBirthday());

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
                    .build();
            userRepository.save(updatedUser);
            logger.debug("Saved updated UserInfo to database");

            logger.info("Successfully updated birthday for user: {} - New value: {}", userId, birthday);
        } catch (Exception e) {
            logger.error("Error occurred while updating birthday info for user: {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 키와 몸무게 정보만을 수정하기 위한 메서드 */
    @Transactional
    public void updateUserPhysicalInfo(Long userId, UserPhysicalInfoDto userPhysicalInfoDto) {
        logger.info("Starting updateUserPhysicalInfo for user: {}", userId);
        try {
            User user = getUserById(userId);
            logger.debug("Retrieved user: {}", user.getId());

            UserInfo userInfo = userInfoRepository.findByUser(user)
                    .orElse(new UserInfo(null, user, null, null, null));
            logger.debug("Retrieved or created UserInfo for user: {}", user.getId());

            logger.info("Updating physical info for user: {} - Old values: height={}, weight={}",
                    userId, userInfo.getHeight(), userInfo.getWeight());

            userInfo.updateInfo(userPhysicalInfoDto.getHeight(), userPhysicalInfoDto.getWeight());

            logger.info("Updated physical info for user: {} - New values: height={}, weight={}",
                    userId, userPhysicalInfoDto.getHeight(), userPhysicalInfoDto.getWeight());

            userInfoRepository.save(userInfo);
            logger.debug("Saved updated UserInfo to database");

            logger.info("Successfully completed updateUserPhysicalInfo for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error occurred while updating physical info for user: {}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void updateUserInfo(Long userId, UserInfoDto userInfoDto) {
        User user = getUserById(userId);

        // 닉네임 업데이트
        if (userInfoDto.getNickname() != null) {
            user.updateProfile(userInfoDto.getNickname(), user.getProfileImage());
        }

        // 생년월일 업데이트
        if (userInfoDto.getBirthday() != null) {
            user = User.builder()
                    .id(user.getId())
                    .kakaoId(user.getKakaoId())
                    .nickName(user.getNickName())
                    .birthday(userInfoDto.getBirthday())
                    .profileImage(user.getProfileImage())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .firedAt(user.getFiredAt())
                    .userStatus(user.getUserStatus())
                    .build();
        }

        userRepository.save(user);

        // 신체 정보 업데이트
        UserInfo userInfo = userInfoRepository.findByUser(user)
                .orElse(new UserInfo(null, user, null, null, null));

        if (userInfoDto.getHeight() != null || userInfoDto.getWeight() != null) {
            userInfo.updateInfo(userInfoDto.getHeight(), userInfoDto.getWeight());
            userInfoRepository.save(userInfo);
        }
    }

    public UserInfoDto getUserInfo(Long userId) {
        logger.info("Starting getUserInfo for user: {}", userId);
        // 이미 유효한 JWT 토큰(인증이 되어 있는)이 있다고 가정
        try {
            if (!testMode) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                    logger.warn("Authentication required for getUserInfo, userId: {}", userId);
                    throw new AccessDeniedException("Authentication required");
                }

                String currentUserId = authentication.getName();
                if (!currentUserId.equals(userId.toString())) {
                    logger.warn("No permission for getUserInfo, userId: {}", userId);
                    throw new AccessDeniedException("You don't have permission to access this resource");
                }
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found for userId: {}", userId);
                        return new RuntimeException("User not found");
                    });
            logger.debug("Retrieved user: {}", user.getId());

            UserInfo userInfo = userInfoRepository.findByUser(user)
                    .orElse(new UserInfo(null, user, null, null, null));
            logger.debug("Retrieved or created UserInfo for user: {}", user.getId());

            UserInfoDto userInfoDto = convertToDto(userInfo);
            logger.info("Successfully retrieved user info for user: {}", userId);
            logger.debug("User info details: {}", userInfoDto);

            return userInfoDto;
        } catch (Exception e) {
            logger.error("Error occurred while getting user info for user: {}", userId, e);
            throw e;
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserInfoDto convertToDto(UserInfo userInfo) {
        return UserInfoDto.builder()
                .nickname(userInfo.getUser().getNickName())
                .birthday(userInfo.getUser().getBirthday())
                .height(userInfo.getHeight())
                .weight(userInfo.getWeight())
                .bmi(userInfo.getBmi())
                .build();
    }
}
