package com.homefit.backend.global.exception.model;

import com.homefit.backend.global.exception.ErrorCode;

public class InternalServerException extends ConflictException{
    public InternalServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InternalServerException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_EXCEPTION);
    }
}
