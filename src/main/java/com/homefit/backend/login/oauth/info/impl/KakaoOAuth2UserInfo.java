package com.homefit.backend.login.oauth.info.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.oauth.info.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                return (String) profile.get("nickname");
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            Boolean emailNeedsAgreement = (Boolean) kakaoAccount.get("email_needs_agreement");
            // email_needs_agreement 확인
            if (emailNeedsAgreement == null || !emailNeedsAgreement) {
                return (String) kakaoAccount.get("email");
            }
        }

        // 이메일이 없는 경우 닉네임을 사용하여 가상의 이메일 생성(nickname@kakao.com)
        String nickname = getName();
        return nickname != null ? nickname + "@kakao.com" : null;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public String getImageUrl() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            // profile_image_needs_agreement 확인
            if (profile != null) {
                Boolean profileImageNeedsAgreement = (Boolean) kakaoAccount.get("profile_image_needs_agreement");
                if (profileImageNeedsAgreement == null || !profileImageNeedsAgreement) {
                    return (String) profile.get("profile_image_url");
                }
            }
        }

        return null;
    }
}
