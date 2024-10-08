package com.homefit.backend.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // Validation Exception: 400
    VALIDATION_EXCEPTION("V001", "잘못된 요청입니다."),
    VALIDATION_PARAMETER_EXCEPTION("V002", "파라미터 값이 잘못 되었습니다."),
    MISSING_REQUIRED_PARAMETER("V003", "필수 파라미터가 누락되었습니다."),
    INVALID_PARAMETER_FORMAT("V004", "잘못된 파라미터 형식 입니다."),

    // Unauthorized Exception: 401
    UNAUTHORIZED_EXCEPTION("U001", "인증되지 않은 접근입니다."),

    // Forbidden Exception: 403
    FORBIDDEN_EXCEPTION("F001", "허용하지 않는 접근입니다."),

    // NotFound Exception: 404
    NOT_FOUND_EXCEPTION("N001", "요청한 리소스를 찾을 수 없습니다."),

    // Method Not Allowed Exception: 405
    METHOD_NOT_ALLOWED_EXCEPTION("M001", "허용되지 않은 메서드입니다."),

    // Conflict Exception: 409
    CONFLICT_EXCEPTION("C001", "중복된 정보입니다."),

    // Unsupported Media Type Exception: 415
    UNSUPPORTED_MEDIA_TYPE("MT01", "지원되지 않는 미디어 타입입니다."),

    // Internal Server Exception: 500
    INTERNAL_SERVER_EXCEPTION("I001", "서버 내부에서 에러가 발생하였습니다.");

    private final String code;
    private final String message;

}
