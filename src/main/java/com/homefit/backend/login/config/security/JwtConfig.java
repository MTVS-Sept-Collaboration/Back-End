package com.homefit.backend.login.config.security;

import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthTokenProvider authTokenProvider() {
        // secret 값을 Base64 디코딩하여 byte 배열로 변환
        byte[] secretKey = Base64.getUrlDecoder().decode(secret);
        return new AuthTokenProvider(secretKey);
    }
}
