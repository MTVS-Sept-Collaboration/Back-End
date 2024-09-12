package com.homefit.backend.login.oauth.service;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.ProviderType;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.entity.user.UserPrincipal;
import com.homefit.backend.login.oauth.info.OAuth2UserInfo;
import com.homefit.backend.login.oauth.info.OAuth2UserInfoFactory;
import com.homefit.backend.login.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return this.processOAuth2User(oAuth2User);
        } catch (Exception ex) {
            log.error("Error while processing OAuth2User", ex);
            throw ex;
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(ProviderType.KAKAO, oAuth2User.getAttributes());

        String kakaoId = userInfo.getId();
        log.info("Processing OAuth2User: {}", kakaoId);

        User user = userRepository.findByKakaoId(kakaoId)
                .map(existingUser -> updateExistingUser(existingUser, userInfo))
                .orElseGet(() -> createNewUser(userInfo));

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User createNewUser(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .kakaoId(userInfo.getId())
                .nickName(userInfo.getName())
                .profileImage(userInfo.getImageUrl())
                .role(RoleType.USER)  // 기본 역할 설정
                .userStatus(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo userInfo) {
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .kakaoId(existingUser.getKakaoId())
                .nickName(userInfo.getName())
                .profileImage(userInfo.getImageUrl())
                .role(existingUser.getRole())
                .userStatus(existingUser.getUserStatus())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .firedAt(existingUser.getFiredAt())
                .build();

        return userRepository.save(updatedUser);
    }
}
