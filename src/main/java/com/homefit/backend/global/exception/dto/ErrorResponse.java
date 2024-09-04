package com.homefit.backend.global.exception.dto;

import com.homefit.backend.global.exception.model.CustomException;
import lombok.Getter;

@Getter
public class ErrorResponse
{
    private final String errorCode;
    private final String message;
    private final String detailMessage;

    public ErrorResponse(CustomException ex) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detailMessage = ex.getMessage();
    }

    public ErrorResponse(CustomException ex, String message) {
        this.errorCode = ex.getErrorCode().getCode();
        this.message = ex.getErrorCode().getMessage();
        this.detailMessage = message;
    }
}
