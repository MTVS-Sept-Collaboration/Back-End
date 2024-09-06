package com.homefit.backend.login.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.common.CustomApiResponse;
import com.homefit.backend.login.common.CookieUtil;
import com.homefit.backend.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.homefit.backend.login.common.CustomApiResponse.UNAUTHORIZED;
import static com.homefit.backend.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String errorMessage = exception.getLocalizedMessage();
        String targetUrl = determineTargetUrl(request, errorMessage);

        if (response.isCommitted()) {
            logger.warn("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        if (isApiRequest(request)) {
            handleApiFailure(response, exception);
        } else {
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

    private String determineTargetUrl(HttpServletRequest request, String errorMessage) {
        String targetUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/"); // 기본 URL을 "/"로 설정

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", errorMessage)
                .build().toUriString();
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private void handleApiFailure(
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String errorMessage = exception instanceof OAuth2AuthenticationException
                ? ((OAuth2AuthenticationException) exception).getError().getErrorCode()
                : exception.getMessage();

        CustomApiResponse<?> customApiResponse = new CustomApiResponse<>(
                UNAUTHORIZED.value(),
                errorMessage,
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(customApiResponse));
    }
}
