package com.homefit.backend.global.exception.model;


import com.homefit.backend.global.exception.ErrorCode;

public class ConflictException extends CustomException {

    public ConflictException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT_EXCEPTION);
    }
}
