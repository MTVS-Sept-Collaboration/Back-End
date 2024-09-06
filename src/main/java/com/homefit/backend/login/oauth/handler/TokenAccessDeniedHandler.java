package com.homefit.backend.login.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.common.CustomApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.homefit.backend.login.common.CustomApiResponse.UNAUTHORIZED;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        log.error("Responding with access denied error. Message := {}", accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CustomApiResponse<?> customApiResponse = new CustomApiResponse<>(
                UNAUTHORIZED.value(),
                accessDeniedException.getMessage(),
                null
        );
        response.getWriter().print(objectMapper.writeValueAsString(customApiResponse));
    }
}