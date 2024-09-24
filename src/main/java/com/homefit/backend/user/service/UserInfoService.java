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
        log.info("사용자 닉네임 수정 시작: 사용자 ID = {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updateNickName(nickName);
            userInfoRepository.save(userInfo);
            log.info("사용자 닉네임 수정 완료: 사용자 ID = {} - 변경된 닉네임: {}", userId, nickName);
        } catch (Exception e) {
            log.error("사용자 닉네임 수정 중 오류 발생: 사용자 ID = {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 생년월일 정보만을 추가(수정)하기 위한 메서드 */
    @Transactional
    public void updateBirthday(Long userId, LocalDate birthday) {
        log.info("사용자 생년월일 수정 시작: 사용자 ID = {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updateBirthday(birthday);
            userInfoRepository.save(userInfo);
            log.info("사용자 생년월일 수정 완료: 사용자 ID = {}, 변경된 생년월일 = {}\"", userId, birthday);
        } catch (Exception e) {
            log.error("사용자 생년월일 수정 중 오류 발생: 사용자 ID = {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 키와 몸무게 정보만을 수정하기 위한 메서드 */
    @Transactional
    public void updateUserPhysicalInfo(Long userId, UserPhysicalInfoDto userPhysicalInfoDto) {
        log.info("사용자 신체 정보 수정 시작: 사용자 ID = {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);
            userInfo.updatePhysicalInfo(userPhysicalInfoDto.getHeight(), userPhysicalInfoDto.getWeight());
            userInfoRepository.save(userInfo);
            log.info("사용자 신체 정보 수정 완료: 사용자 ID = {}, 키 = {}, 몸무게 = {}", userId, userPhysicalInfoDto.getHeight(), userPhysicalInfoDto.getWeight());
        } catch (Exception e) {
            log.error("사용자 신체 정보 수정 중 오류 발생: 사용자 ID = {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 모든 정보(닉네임, 생년월일, 키, 몸무게)를 수정하기 위한 메서드 */
    @Transactional
    public void updateUserInfo(Long userId, UserInfoDto userInfoDto) {
        log.info("사용자 전체 정보 수정 시작: 사용자 ID = {}", userId);
        try {
            UserInfo userInfo = getUserInfoById(userId);

            if (userInfoDto.getNickName() != null) {
                userInfo.updateNickName(userInfoDto.getNickName());
            }
            if (userInfoDto.getBirthday() != null) {
                userInfo.updateBirthday(userInfoDto.getBirthday());
            }
            userInfo.updatePhysicalInfo(userInfoDto.getHeight(), userInfoDto.getWeight());
            userInfoRepository.save(userInfo);
            log.info("사용자 전체 정보 수정 완료: 사용자 ID = {}", userId);
        } catch (Exception e) {
            log.error("사용자 전체 정보 수정 중 오류 발생: 사용자 ID = {}", userId, e);
            throw e;
        }
    }

    /** 사용자의 모든 정보(닉네임, 생년월일, 키, 몸무게)를 수정하기 위한 메서드 */
    @Transactional
    public UserInfoDto getUserInfo(Long userId) {
        log.info("사용자 정보 조회 시작: 사용자 ID = {}", userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            UserInfo userInfo = userInfoRepository.findByUser(user)
                    .orElseGet(() -> createDefaultUserInfo(user));

            UserInfoDto userInfoDto = convertToDto(userInfo);
            log.info("사용자 정보 조회 완료: 사용자 ID = {}", userId);
            return userInfoDto;
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류 발생: 사용자 ID = {}", userId, e);
            throw e;
        }
    }

    private UserInfo getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return userInfoRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
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
