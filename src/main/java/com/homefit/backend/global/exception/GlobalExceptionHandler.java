package com.homefit.backend.global.exception;

import com.homefit.backend.global.exception.dto.ErrorResponse;
import com.homefit.backend.global.exception.model.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String VALIDATION_MESSAGE = "Validation error occurred: ";
    private static final String UNAUTHORIZED_MESSAGE = "Unauthorized error occurred: ";
    private static final String METHOD_NOT_ALLOWED_MESSAGE = "Method not allowed occurred: ";
    private static final String CONFLICT_MESSAGE = "Conflict error occurred: ";
    private static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported Media Type error occurred: ";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error occurred: ";
    private static final String METHOD_NOT_ALLOWED_ERROR_MESSAGE = "Method not allowed. Allowed method: %s";


    // Validation 예외 처리
    @ExceptionHandler({
            ValidationException.class,
            UnsatisfiedServletRequestParameterException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        logWarn(VALIDATION_MESSAGE, ex);

        ErrorCode errorCode;
        if (ex instanceof UnsatisfiedServletRequestParameterException) {
            errorCode = ErrorCode.MISSING_REQUIRED_PARAMETER;
        } else if (ex instanceof MethodArgumentNotValidException) {
            errorCode = ErrorCode.VALIDATION_PARAMETER_EXCEPTION;
        } else {
            errorCode = ErrorCode.INVALID_PARAMETER_FORMAT;
        }

        CustomException customException = new ValidationException(ex.getMessage(), errorCode);
        return new ResponseEntity<>(new ErrorResponse(customException), HttpStatus.BAD_REQUEST);
    }

    // 인증 오류 처리
    @ExceptionHandler({
            UnauthorizedException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(CustomException ex) {
        logWarn(UNAUTHORIZED_MESSAGE, ex);
        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.UNAUTHORIZED);
    }

    // 접근 금지 오류 처리
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        logWarn("접근 금지 오류 발생: ", ex);
        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.FORBIDDEN);
    }

    // 리소스 찾을 수 없음 오류 처리
    @ExceptionHandler({NotFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex) {
        logWarn("리소스 찾을 수 없음 오류 발생: ", ex);

        ErrorCode errorCode;
        if (ex instanceof NoHandlerFoundException) {
            // NoHandlerFoundException의 경우
            errorCode = ErrorCode.NOT_FOUND_EXCEPTION;
        } else {
            // NotFoundException의 경우
            errorCode = ErrorCode.NOT_FOUND_EXCEPTION;
        }

        CustomException customException;
        if (ex instanceof NoHandlerFoundException) {
            customException = new NotFoundException(ex.getMessage(), errorCode);
        } else {
            customException = (NotFoundException) ex;
        }

        ErrorResponse response = new ErrorResponse(customException);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 중복 데이터 오류 처리
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        logWarn(CONFLICT_MESSAGE, ex);
        return new ResponseEntity<>(new ErrorResponse(ex), HttpStatus.CONFLICT);
    }

    // 지원하지 않는 미디어 타입 오류 처리
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logWarn(UNSUPPORTED_MEDIA_TYPE, ex);

        CustomException customException = new UnsupportedMediaTypeException(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(customException), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 허용되지 않는 메서드 오류 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        logWarn(METHOD_NOT_ALLOWED_MESSAGE, ex);

        // 허용 가능한 메서드를 알려주는 메시지 작성
        String allowedMethods = ex.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.joining(", "));
        String message = String.format(METHOD_NOT_ALLOWED_ERROR_MESSAGE, allowedMethods);
        CustomException customException = new UnsupportedMethodTypeException(message);

        // 405 상태코드는 Allow 헤더를 포함시켜야 한다.
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.ALLOW, allowedMethods)
                .body(new ErrorResponse(customException, message));
    }

    // 잘못된 인수 오류 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        CustomException customException = new ValidationException(ex.getMessage(), ErrorCode.INVALID_PARAMETER_FORMAT);
        return new ResponseEntity<>(new ErrorResponse(customException), HttpStatus.BAD_REQUEST);
    }

    // 서버 내부 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception ex) {
        logError(INTERNAL_SERVER_ERROR_MESSAGE, ex);

        CustomException customException = new InternalServerException(ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(customException), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // 경고 로그 출력
    private void logWarn(String message, Exception ex) {
        logger.warn(message, ex);
    }

    // 에러 로그 출력
    private void logError(String message, Exception ex) {
        logger.error(message, ex);
    }



}
