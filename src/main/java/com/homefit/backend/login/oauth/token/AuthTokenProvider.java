package com.homefit.backend.login.oauth.token;

import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.exception.TokenValidFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenProvider {

    private final Key key;
    @Getter
    private final long expiration;
    private static final String AUTHORITIES_KEY = "role";

    public AuthTokenProvider(byte[] secretKey, long expiration) {
        this.key = Keys.hmacShaKeyFor(secretKey);
        this.expiration = expiration;
    }

    public AuthToken createAuthToken(String id, String role) {
        return createAuthToken(id, role, new Date(System.currentTimeMillis() + expiration));
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        AuthToken token = new AuthToken(id, role, expiry, key);
        log.debug("Created AuthToken: id={}, role={}, expiry={}", id, role, expiry);
        return token;
    }

    public AuthToken convertAuthToken(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Attempting to convert null or empty token");
            return null;
        }

        try {
            return new AuthToken(token, key);
        } catch (Exception e) {
            log.error("Error converting token: {}", token, e);
            return null;
        }
    }

    public Authentication getAuthentication(AuthToken authToken) {
        if (authToken.validate()) {
            Claims claims = authToken.getTokenClaims();
            String role = claims.get(AUTHORITIES_KEY, String.class); // 역할 정보 추출
            log.debug("Extracted role from token: {}", role);

            // role이 null일 경우 기본값 설정
            if (role == null) {
                role = RoleType.USER.getCode();
                log.debug("No role found in token, using default: {}", role);
            }

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(role.split(",")) // 역할이 여러 개인 경우 처리
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            log.debug("Authorities extracted from token: {}", authorities);

            User principal = new User(claims.getSubject(), "", authorities);
            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            log.warn("Token validation failed");
            throw new TokenValidFailedException();
        }
    }
}
