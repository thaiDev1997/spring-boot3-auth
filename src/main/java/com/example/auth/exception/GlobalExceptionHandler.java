package com.example.auth.exception;

import com.example.auth.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(){
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER;
        return ResponseEntity.badRequest()
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
}
