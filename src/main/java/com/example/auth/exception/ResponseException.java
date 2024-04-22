package com.example.auth.exception;

import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException{
    private ErrorCode errorCode;
    public ResponseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
