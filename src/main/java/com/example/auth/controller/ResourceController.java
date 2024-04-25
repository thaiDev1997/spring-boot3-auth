package com.example.auth.controller;

import com.example.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/resource")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ResourceController {

    UserService userService;

    @GetMapping(value = "/authentication-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> authenticationInfo(Authentication authentication) {
        return ((Jwt) authentication.getPrincipal()).getClaims();
    }
}
