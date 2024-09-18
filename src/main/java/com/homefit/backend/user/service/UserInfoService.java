package com.homefit.backend.user.service;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

    @Setter
    private boolean testMode = false; // 테스트 모드 활성화(이미 인증이 되어 있다고 가정)

    /** 사용자의 닉네임 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateNickName(Long userId, String nickName) {
        log.info("Starting updateNickname for user: {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updateNickName(nickName);
            userInfoRepository.save(userInfo);
            log.info("Successfully updated nickName for user: {} - New value: {}", userId, nickName);
        } catch (Exception e) {
            log.error("Error occurred while updating nickName info for user: {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 생년월일 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateBirthday(Long userId, LocalDate birthday) {
        log.info("Starting updateBirthday for user: {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updateBirthday(birthday);
            userInfoRepository.save(userInfo);
            log.info("Successfully updated birthday for user: {} - New value: {}", userId, birthday);
        } catch (Exception e) {
            log.error("Error occurred while updating birthday info for user: {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 키와 몸무게 정보만을 수정하기 위한 메서드 */
    @Transactional
    public void updateUserPhysicalInfo(Long userId, UserPhysicalInfoDto userPhysicalInfoDto) {
        log.info("Starting updateUserPhysicalInfo for user: {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updatePhysicalInfo(userPhysicalInfoDto.getHeight(), userPhysicalInfoDto.getWeight());
            userInfoRepository.save(userInfo);
            log.info("Successfully completed updateUserPhysicalInfo for user: {}", userId);
        } catch (Exception e) {
            log.error("Error occurred while updating physical info for user: {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 모든 정보(닉네임, 생년월일, 키, 몸무게)를 수정하기 위한 메서드 */
    @Transactional
    public void updateUserInfo(Long userId, UserInfoDto userInfoDto) {
        UserInfo userInfo = getUserInfoById(userId);

        if (userInfoDto.getNickName() != null) {
            userInfo.updateNickName(userInfoDto.getNickName());
        }
        if (userInfoDto.getBirthday() != null) {
            userInfo.updateBirthday(userInfoDto.getBirthday());
        }
        userInfo.updatePhysicalInfo(userInfoDto.getHeight(), userInfoDto.getWeight());

        userInfoRepository.save(userInfo);
    }

    /** 사용자의 모든 정보(닉네임, 생년월일, 키, 몸무게)를 수정하기 위한 메서드 */
    @Transactional
    public UserInfoDto getUserInfo(Long userId) {
        log.info("Starting getUserInfo for user: {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserInfo userInfo = userInfoRepository.findByUser(user)
                    .orElseGet(() -> createDefaultUserInfo(user));

            return convertToDto(userInfo);
        } catch (Exception e) {
            log.error("Error occurred while getting user info for user: {}", userId, e);
            throw e;
        }
    }

    private UserInfo getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userInfoRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("UserInfo not found"));
    }

    private UserInfo createDefaultUserInfo(User user) {
        UserInfo newUserInfo = new UserInfo(null, user, user.getUserName(), null, null, null);
        return userInfoRepository.save(newUserInfo);
    }

    private UserInfoDto convertToDto(UserInfo userInfo) {
        return UserInfoDto.builder()
                .nickName(userInfo.getNickName())
                .birthday(userInfo.getBirthday())
                .height(userInfo.getHeight())
                .weight(userInfo.getWeight())
                .bmi(userInfo.getBmi())
                .build();
    }
}
