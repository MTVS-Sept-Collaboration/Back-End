package com.homefit.backend.global.exception.model;


import com.homefit.backend.global.exception.ErrorCode;

public class ForbiddenException extends ConflictException{
    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN_EXCEPTION);
    }
}
