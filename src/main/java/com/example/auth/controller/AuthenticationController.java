package com.example.auth.controller;

import com.example.auth.dto.request.AuthenticationRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {

  AuthenticationService authenticationService;

  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<String> authenticate(
      @RequestBody AuthenticationRequest authenticationRequest) {
    String token = authenticationService.authenticate(authenticationRequest);
    return ApiResponse.<String>builder().message("Login successfully!").result(token).build();
  }

  @PostMapping(value = "/user-details-service/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ApiResponse<String> authenticateByUserDetailsService(
      @RequestBody AuthenticationRequest authenticationRequest) {
    String token = authenticationService.authenticateByUserDetailsService(authenticationRequest);
    return ApiResponse.<String>builder().message("Login successfully!").result(token).build();
  }

  @DeleteMapping(value = "/logout")
  public void logout(Authentication authentication) {
    authenticationService.logout(((Jwt) authentication.getPrincipal()));
  }

  @PostMapping(value = "/refresh-token")
  public ApiResponse<String> refreshToken(Authentication authentication) {
    String token = authenticationService.refreshToken(((Jwt) authentication.getPrincipal()));
    return ApiResponse.<String>builder()
        .message("Refresh token successfully!")
        .result(token)
        .build();
  }
}
