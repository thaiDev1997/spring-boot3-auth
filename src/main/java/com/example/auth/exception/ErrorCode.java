package com.example.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER(0, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PASSWORD(1, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(2, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(3, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    INVALID_ROLE_PERMISSION(3, "Invalid Role Permission", HttpStatus.FORBIDDEN);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}