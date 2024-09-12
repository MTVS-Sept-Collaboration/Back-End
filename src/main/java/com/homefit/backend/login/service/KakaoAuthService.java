package com.homefit.backend.login.service;

import com.homefit.backend.login.config.properties.AppProperties;
import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.info.impl.KakaoOAuth2UserInfo;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings(value = "unchecked")
public class KakaoAuthService {
    private static final Logger logger = LoggerFactory.getLogger(KakaoAuthService.class);

    private final UserRepository userRepository;
    private final AuthTokenProvider tokenProvider;
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final ServletContext servletContext;

    @Transactional
    public String loginWithKakaoToken(String kakaoAccessToken) {
        KakaoOAuth2UserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);
        User user = processUserLogin(userInfo);
        String jwtToken = createNewJwtToken(user);
        user.updateLoginInfo(LocalDateTime.now(), jwtToken);
        return jwtToken;
    }

    private KakaoOAuth2UserInfo getKakaoUserInfo(String accessToken) {
        String userInfoEndpoint = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoEndpoint,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            log.debug("Successfully retrieved user info from Kakao API");
            return new KakaoOAuth2UserInfo(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching user info from Kakao API", e);
            throw new RuntimeException("Failed to get Kakao user info", e);
        }
    }

    public String getKakaoAccessToken(String code) {
        log.debug("Fetching Kakao access token with authorization code: {}", code);
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", appProperties.getKakao().getClientId());
        params.add("client_secret", appProperties.getKakao().getClientSecret());
        params.add("redirect_uri", appProperties.getKakao().getRedirectUri());
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            log.info("Successfully retrieved Kakao access token");
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            log.error("Error fetching Kakao access token. Status code: {}, Response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get Kakao access token", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching Kakao access token", e);
            throw new RuntimeException("Failed to get Kakao access token", e);
        }
    }

    private String buildRedirectUri(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = servletContext.getContextPath();

        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(serverName)
                .port(serverPort)
                .path(contextPath)
                .path("/oauth2/kakao/callback")
                .build()
                .toUriString();
    }

    private String createNewJwtToken(User user) {
        Date now = new Date();
        long tokenValidityInMilliseconds = appProperties.getAuth().getTokenExpirationMsec();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return tokenProvider.createAuthToken(
                user.getId().toString(),
                user.getRole().name(),
                validity
        ).getToken();
    }

    public String getKakaoLoginUrl(HttpServletRequest request) {
        String baseUrl = "https://kauth.kakao.com/oauth/authorize";
        String clientId = appProperties.getKakao().getClientId();
        String redirectUri = buildRedirectUri(request);

        log.info("Generating Kakao login URL with client ID: {} and redirect URI: {}", clientId, redirectUri);

        String loginUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .build(true)  // URI 인코딩 적용
                .toUriString();

        log.info("Generated Kakao login URL: {}", loginUrl);

        return loginUrl;
    }

    private User processUserLogin(KakaoOAuth2UserInfo userInfo) {
        return userRepository.findByKakaoId(userInfo.getId())
                .map(existingUser -> {
                    existingUser.updateProfile(userInfo.getName(), userInfo.getImageUrl());
                    return userRepository.save(existingUser);  // Always save existing user
                })
                .orElseGet(() -> createNewUser(userInfo));
    }

    private User createNewUser(KakaoOAuth2UserInfo userInfo) {
        User newUser = User.builder()
                .kakaoId(userInfo.getId())
                .nickName(userInfo.getName())
                .profileImage(userInfo.getImageUrl())
                .role(RoleType.USER)
                .userStatus(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(newUser);
    }

    private User convertToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .kakaoId(userDto.getKakaoId())
                .nickName(userDto.getNickName())
                .profileImage(userDto.getProfileImage())
                .role(userDto.getRole())
                .userStatus(userDto.getUserStatus())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .firedAt(userDto.getFiredAt())
                .build();
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
                .build();
    }
}
