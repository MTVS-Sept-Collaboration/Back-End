package com.homefit.backend.login.oauth.filter;

import com.homefit.backend.login.common.CookieUtil;
import com.homefit.backend.login.oauth.entity.user.UserPrincipal;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.homefit.backend.login.common.HeaderUtil.ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String tokenValue = CookieUtil.getCookie(request, ACCESS_TOKEN)
                .map(Cookie::getValue)
                .orElse(null);

        if (StringUtils.hasText(tokenValue)) {
            AuthToken token = tokenProvider.convertAuthToken(tokenValue);
            if (token.validate()) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // SecurityContext에 토큰 정보 저장
                if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userPrincipal, token, authentication.getAuthorities())
                    );
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
