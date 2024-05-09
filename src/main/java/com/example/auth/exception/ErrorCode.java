package com.example.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
  INTERNAL_SERVER(0, "Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_PASSWORD(1, "Invalid password", HttpStatus.BAD_REQUEST),
  USER_NOT_EXISTED(2, "User not existed", HttpStatus.NOT_FOUND),
  UNAUTHENTICATED(3, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  INVALID_ROLE_PERMISSION(4, "Invalid Role Permission", HttpStatus.FORBIDDEN),
  INVALID_REQUEST(5, "Bad Request", HttpStatus.BAD_REQUEST),
  NO_RESOURCE_FOUND(6, "No Resource Found", HttpStatus.NOT_FOUND),
  ACCOUNT_EXISTED(7, "Account existed", HttpStatus.BAD_REQUEST),
  ;

  ErrorCode(int code, String message, HttpStatusCode statusCode) {
    this.code = code;
    this.message = message;
    this.statusCode = statusCode;
  }

  private int code;
  private String message;
  private HttpStatusCode statusCode;
}
