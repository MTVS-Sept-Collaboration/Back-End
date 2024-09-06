package com.homefit.backend.login.service;

import com.homefit.backend.login.config.properties.AppProperties;
import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.info.OAuth2UserInfo;
import com.homefit.backend.login.oauth.info.impl.KakaoOAuth2UserInfo;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final AuthTokenProvider tokenProvider;
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    @Transactional
    public String loginWithKakaoToken(String kakaoAccessToken) {
        try {
            // 1. 카카오 API를 호출하여 사용자 정보 가져오기
            Map<String, Object> userInfoMap = getKakaoUserInfo(kakaoAccessToken);

            // 2. Map을 KakaoOAuth2UserInfo 객체로 변환
            OAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(userInfoMap);

            // 3. 사용자 정보로 회원가입 또는 로그인 처리
            UserDto userDto = processUserRegistration(userInfo);

            // 4. JWT 토큰 생성
            Date now = new Date();
            AuthToken authToken = tokenProvider.createAuthToken(
                    userDto.getId().toString(),
                    userDto.getRole().getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
            );

            return authToken.getToken();
        } catch (Exception e) {
            log.error("Error in loginWithKakaoToken", e);
            throw new RuntimeException("Failed to process Kakao login", e);
        }
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoEndpoint = "https://kapi.kakao.com/v2/user/me";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setBearerAuth(accessToken);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(
                userInfoEndpoint,
                GET,
                entity,
                Map.class
        );

        return response.getBody();
    }

    private UserDto processUserRegistration(OAuth2UserInfo userInfo) {
        // 기존 사용자 확인
        User user = userRepository.findByKakaoId(userInfo.getId())
                .map(existingUser -> {
                    existingUser.updateProfile(userInfo.getName(), userInfo.getImageUrl());
                    return existingUser;
                })
                .orElseGet(() -> User.builder()
                        .kakaoId(userInfo.getId())
                        .nickName(userInfo.getName())
                        .profileImage(userInfo.getImageUrl())
                        .role(RoleType.USER)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .userStatus(true)
                        .build());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .nickName(user.getNickName())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .firedAt(user.getFiredAt())
                .userStatus(user.getUserStatus())
                .refreshToken(user.getRefreshToken())
                .build();
    }
}
