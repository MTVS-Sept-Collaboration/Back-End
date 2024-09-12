package com.homefit.backend.login.config.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final Kakao kakao = new Kakao();

    @Getter
    @Setter
    public static class Auth {
        @Value("${jwt.secret}")
        private String tokenSecret;

        @Value("${jwt.expiration}")
        private long tokenExpiry;

        @Value("${jwt.expiration}")
        private long refreshTokenExpiry;

        public long getTokenExpirationMsec() {
            return tokenExpiry;
        }

        public long getRefreshTokenExpirationMsec() {
            return refreshTokenExpiry;
        }
    }

    @Getter
    @Setter
    public static class Kakao {
        @Value("${app.kakao.client-id}")
        private String clientId;

        @Value("${app.kakao.client-secret}")
        private String clientSecret;

        @Value("${app.kakao.redirect-uri}")
        private String redirectUri;
    }

    @PostConstruct
    public void logProperties() {
        log.info("JWT Secret: {}", auth.getTokenSecret());
        log.info("JWT Expiration: {}", auth.getTokenExpiry());
        log.info("Kakao Client ID: {}", kakao.getClientId());
        log.info("Kakao Client Secret: {}", kakao.getClientSecret());
        log.info("Kakao Redirect URI: {}", kakao.getRedirectUri());
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }
}
