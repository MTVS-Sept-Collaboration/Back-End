package com.homefit.backend.global.exception.model;

import com.homefit.backend.global.exception.ErrorCode;

public class UnauthorizedException extends ConflictException{
    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED_EXCEPTION);
    }
}
