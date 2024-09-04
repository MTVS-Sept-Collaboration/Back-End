package com.homefit.backend.global.exception.model;

import com.homefit.backend.global.exception.ErrorCode;

public class UnsupportedMethodTypeException extends CustomException {
    public UnsupportedMethodTypeException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnsupportedMethodTypeException(String message) {
        super(message, ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION);
    }
}
