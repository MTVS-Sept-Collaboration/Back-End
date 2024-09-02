package com.homefit.backend.login.oauth.info;

import com.homefit.backend.login.oauth.entity.ProviderType;
import com.homefit.backend.login.oauth.info.impl.KakaoOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(
            ProviderType providerType,
            Map<String, Object> attributes
    ) {
        return switch (providerType) {
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Invalid Provider Type.");
        };
    }
}
