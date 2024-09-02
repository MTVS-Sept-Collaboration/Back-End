package com.homefit.backend.login.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import java.util.Base64;
import java.util.Optional;

@Slf4j
public class CookieUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModules(SecurityJackson2Modules.getModules(CookieUtil.class.getClassLoader()))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static Optional<Cookie> getCookie(
            HttpServletRequest request,
            String name
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // XSS 공격 방지를 위해 HttpOnly 설정
        cookie.setSecure(true); // HTTPS 환경에서만 쿠키 전송
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax"); // 또는 "Strict"
        response.addCookie(cookie);
        log.debug("Added cookie: name={}, value={}, maxAge={}", name, value, maxAge);
    }

    public static void deleteCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키 만료
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return Base64.getUrlEncoder().encodeToString(json.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("직렬화 오류", e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            String json = new String(Base64.getUrlDecoder().decode(cookie.getValue()));
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            log.error("역직렬화 오류", e);
            return null;
        }
    }
}
