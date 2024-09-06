package com.homefit.backend.login.oauth.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public abstract String getId();

    public abstract String getName();

    public abstract String getImageUrl();
}
