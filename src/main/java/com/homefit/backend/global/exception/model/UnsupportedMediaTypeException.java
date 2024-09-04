package com.homefit.backend.global.exception.model;

import com.homefit.backend.global.exception.ErrorCode;

public class UnsupportedMediaTypeException extends ConflictException{
    public UnsupportedMediaTypeException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UnsupportedMediaTypeException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
}
