package com.homefit.backend.global.exception.model;

import com.homefit.backend.global.exception.ErrorCode;

public class NotFoundException extends ConflictException{
    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND_EXCEPTION);
    }
}
