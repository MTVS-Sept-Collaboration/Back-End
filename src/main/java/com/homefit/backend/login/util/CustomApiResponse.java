package com.homefit.backend.login.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class CustomApiResponse<T> {

    // HttpStatus 상수 정의
    public static final HttpStatus SUCCESS = HttpStatus.OK;
    public static final HttpStatus CREATED = HttpStatus.CREATED;
    public static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    public static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;
    public static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;
    public static final HttpStatus CONFLICT = HttpStatus.CONFLICT;
    public static final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;

    // 메시지 상수 정의
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final String UNAUTHORIZED_MESSAGE = "UNAUTHORIZED";
    public static final String INVALID_ACCESS_TOKEN = "INVALID ACCESS TOKEN.";
    public static final String INVALID_REFRESH_TOKEN = "INVALID REFRESH TOKEN.";
    public static final String NOT_EXPIRED_TOKEN_YET = "NOT EXPIRED TOKEN YET.";

    private final int status;
    private final String message;
    private final T data;

    public static <T> ResponseEntity<CustomApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new CustomApiResponse<>(SUCCESS.value(), message, data));
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> created(T data) {
        return ResponseEntity.status(CREATED).body(new CustomApiResponse<>(CREATED.value(), SUCCESS_MESSAGE, data));
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> error(String message) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse<>(INTERNAL_SERVER_ERROR.value(), message, null));
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new CustomApiResponse<>(status.value(), message, null));
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> fail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body((new CustomApiResponse<>(status.value(), message, null)));
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> badRequest(String message) {
        return fail(BAD_REQUEST, message);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> unauthorized() {
        return fail(UNAUTHORIZED, UNAUTHORIZED_MESSAGE);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> notFound(String message) {
        return fail(NOT_FOUND, message);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> conflict(String message) {
        return fail(CONFLICT, message);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> internalServerError(String message) {
        return fail(INTERNAL_SERVER_ERROR, message);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> invalidAccessToken() {
        return fail(UNAUTHORIZED, INVALID_ACCESS_TOKEN);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> invalidRefreshToken() {
        return fail(UNAUTHORIZED, INVALID_REFRESH_TOKEN);
    }

    public static <T> ResponseEntity<CustomApiResponse<T>> notExpiredTokenYet() {
        return fail(BAD_REQUEST, NOT_EXPIRED_TOKEN_YET);
    }
}
