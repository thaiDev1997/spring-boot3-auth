package com.example.auth.exception;

import com.example.auth.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception){
        log.error(exception.getMessage(), exception);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER;
        return ResponseEntity.internalServerError()
                .body(new ApiResponse(errorCode.getCode(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse> handlingNoResourceFoundException(NoResourceFoundException exception){
        log.error(exception.getMessage(), exception);
        ErrorCode errorCode = ErrorCode.NO_RESOURCE_FOUND;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(new ApiResponse(errorCode.getCode(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(value = ResponseException.class)
    ResponseEntity<ApiResponse> handlingResponseException(ResponseException exception){
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ApiResponse(errorCode.getCode(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.INVALID_ROLE_PERMISSION;
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ApiResponse(errorCode.getCode(), errorCode.getMessage(), null));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, Object> fieldErrorMap = new HashMap<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            fieldErrorMap.put(fieldError.getField(), fieldError.getRejectedValue());
        }
        FieldError fieldError = exception.getFieldError();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(new ApiResponse(errorCode.getCode(), String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()),
                        fieldErrorMap));
    }
}
