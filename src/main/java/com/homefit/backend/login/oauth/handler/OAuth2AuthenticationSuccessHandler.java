package com.homefit.backend.login.oauth.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.common.CookieUtil;
import com.homefit.backend.login.config.properties.AppProperties;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.entity.user.UserPrincipal;
import com.homefit.backend.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.homefit.backend.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // OAuth2AuthenticationToken에서 카카오 액세스 토큰 추출
//        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
//        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(
//                OAuth2AuthorizeRequest.withClientRegistrationId(authToken.getAuthorizedClientRegistrationId())
//                        .principal(authentication)
//                        .attributes(attrs -> attrs.put(HttpServletRequest.class.getName(), request))
//                        .attributes(attrs -> attrs.put(HttpServletResponse.class.getName(), response))
//                        .build()
//        );

        // OAuth2AuthenticationToken에서 카카오 액세스 토큰 추출
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName());

        if (authorizedClient == null) {
            throw new IllegalStateException("AuthorizedClient not found");
        }

        String kakaoAccessToken = authorizedClient.getAccessToken().getTokenValue();

        // UserPrincipal에서 사용자 정보 추출
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // 인증 정보 생성
        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("accessToken", kakaoAccessToken);
        authInfo.put("userId", userPrincipal.getId().toString());

        // 인증 정보를 JSON으로 직렬화
        String authInfoJson;
        try {
            authInfoJson = objectMapper.writeValueAsString(authInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize auth info", e);
        }

        // URL 인코딩된 인증 정보를 타겟 URL에 추가
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("auth_info", URLEncoder.encode(authInfoJson, StandardCharsets.UTF_8))
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

//    private boolean hasAdminRole(Authentication authentication) {
//        return authentication.getAuthorities().stream()
//                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(RoleType.ADMIN.getCode()));
//    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
