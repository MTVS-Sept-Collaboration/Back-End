package com.homefit.backend.login.oauth.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class TokenValidFailedException extends OAuth2AuthenticationException {

    public TokenValidFailedException() {
        super(new OAuth2Error("invalid_token", "Failed to generate Token.", null));
    }
}
