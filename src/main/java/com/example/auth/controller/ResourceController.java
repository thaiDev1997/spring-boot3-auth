package com.example.auth.controller;

import com.example.auth.entity.User;
import com.example.auth.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

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

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping(value = "/users/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable(value = "code") String code) {
        return userService.get(code);
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody User newUser) {
        return userService.create(newUser);
    }

    @PutMapping(value = "/users")
    public void updateUser(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping(value = "/users/{code}")
    public boolean deleteUser(@PathVariable(value = "code") String code) {
        return userService.delete(code);
    }
}
