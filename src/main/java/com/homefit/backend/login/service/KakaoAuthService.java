package com.homefit.backend.login.service;

import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.ProviderType;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.info.OAuth2UserInfo;
import com.homefit.backend.login.oauth.info.OAuth2UserInfoFactory;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final UserRepository userRepository;
    private final AuthTokenProvider authTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }

    @Transactional
    public String loginWithKakaoToken(String kakaoAccessToken) {
        // 1. 카카오 API로 사용자 정보 가져오기
        Map<String, Object> userAttributes = getKakaoUserInfo(kakaoAccessToken);

        // 2. 사용자 정보로 OAuth2UserInfo 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(ProviderType.KAKAO, userAttributes);

        // 3. 사용자 정보로 회원가입 또는 로그인 처리
        UserDto userDto = processUserRegistration(userInfo);

        // 4. JWT 토큰 유효 시간 설정
        Date tokenExpiry = new Date(System.currentTimeMillis() + 3600000); // 1시간 유효

        // 5. JWT 토큰 생성
        AuthToken authToken = authTokenProvider.createAuthToken(userDto.getId().toString(), userDto.getRole().getCode(), tokenExpiry);

        // 6. JWT 토큰 반환
        return authToken.getToken();
    }

    private UserDto processUserRegistration(OAuth2UserInfo userInfo) {
        // 기존 사용자 확인
        User existingUser = userRepository.findByUserName(userInfo.getId()).orElse(null);
        UserDto userDto;

        if (existingUser == null) {
            // 새 사용자 등록
            userDto = UserDto.builder()
                    .userName(userInfo.getId())
                    .email(userInfo.getEmail())
                    .nickName(userInfo.getName())
                    .profileImage(userInfo.getImageUrl())
                    .role(RoleType.USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .userStatus(true)
                    .build();
        } else {
            userDto = UserDto.builder()
                    .id(existingUser.getId())
                    .userName(existingUser.getUserName())
                    .email(userInfo.getEmail())
                    .nickName(userInfo.getName())
                    .profileImage(userInfo.getImageUrl())
                    .role(existingUser.getRole())
                    .createdAt(existingUser.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .userStatus(existingUser.getUserStatus())
                    .refreshToken(existingUser.getRefreshToken())
                    .build();
        }

        User savedUser = userRepository.save(convertToEntity(userDto));
        userDto.setId(savedUser.getId());  // 새로 생성된 경우 ID 설정

        return userDto;
    }

    private User convertToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .nickName(userDto.getNickName())
                .profileImage(userDto.getProfileImage())
                .role(userDto.getRole())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .firedAt(userDto.getFiredAt())
                .userStatus(userDto.getUserStatus())
                .refreshToken(userDto.getRefreshToken())
                .build();
    }
}
