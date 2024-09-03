package com.homefit.backend.login.oauth.service;

import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.ProviderType;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.entity.user.UserPrincipal;
import com.homefit.backend.login.oauth.info.OAuth2UserInfo;
import com.homefit.backend.login.oauth.info.OAuth2UserInfoFactory;
import com.homefit.backend.login.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("OAuth2User loaded: {}", oAuth2User);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred while processing OAuth2 user", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private UserPrincipal processOAuth2User(
            OAuth2UserRequest userRequest,
            OAuth2User oAuth2User
    ) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(ProviderType.KAKAO, oAuth2User.getAttributes());

        log.info("Processing OAuth2User: {}", userInfo.getId());
        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());

        User user = userRepository.findByUserName(userInfo.getId()).orElse(null);

        if (user != null) {
            log.info("Existing user found: {}", user);
            if (!user.getUserStatus()) {
                // 탈퇴한 사용자가 다시 로그인한 경우
                log.info("Attempting to reactivate user account. User details: {}", user);
                UserDto userDto = updateUser(user, userInfo);
                user = userRepository.save(convertToEntity(userDto));
                log.info("User after reactivation: {}", user);
            } else {
                UserDto userDto = updateUser(user, userInfo);
                user = userRepository.save(convertToEntity(userDto));
            }
        } else {
            log.info("Creating new user for KakaoId: {}", userInfo.getId());
            UserDto userDto = createUser(userInfo);
            user = userRepository.save(convertToEntity(userDto));
        }

        // UserPrincipal 생성 시 userId를 name으로 설정
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .profileImage(user.getProfileImage())
                .attributes(oAuth2User.getAttributes())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getCode())))
                .build();

        log.info("Processed user: id={}, userName={}, email={}, nickName={}, profileImage={}", user.getId(), user.getUserName(), user.getEmail(), user.getNickName(), user.getProfileImage());
        return userPrincipal;
    }

    private UserDto createUser(OAuth2UserInfo userInfo) {
        return UserDto.builder()
                .userName(userInfo.getId())
                .email(userInfo.getEmail())
                .nickName(userInfo.getName())
                .profileImage(userInfo.getImageUrl())
                .role(RoleType.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .userStatus(true)
                .build();
    }

    private UserDto updateUser(User user, OAuth2UserInfo userInfo) {
        return UserDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(userInfo.getEmail() != null ? userInfo.getEmail() : user.getEmail())
                .nickName(userInfo.getName() != null ? userInfo.getName() : user.getNickName())
                .profileImage(userInfo.getImageUrl() != null ? userInfo.getImageUrl() : user.getProfileImage())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .firedAt(null)
                .userStatus(true)
                .refreshToken(user.getRefreshToken())
                .build();
    }

    private User convertToEntity(UserDto userDto) {
        return new User(
                userDto.getUserName(),
                userDto.getEmail(),
                userDto.getNickName(),
                userDto.getProfileImage(),
                userDto.getCreatedAt(),
                userDto.getUpdatedAt(),
                userDto.getFiredAt(),
                userDto.getUserStatus(),
                userDto.getRefreshToken()
        );
    }
}
