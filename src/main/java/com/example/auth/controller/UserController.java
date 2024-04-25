package com.example.auth.controller;

import com.example.auth.dto.request.UserCreation;
import com.example.auth.entity.User;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(value = "/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getUser(@PathVariable(value = "code") String code) {
        return userService.get(code);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody @Valid UserCreation newUser) {
        log.info("Controller: create user");
        return userService.create(newUser);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping(value = "/{code}")
    public boolean deleteUser(@PathVariable(value = "code") String code) {
        return userService.delete(code);
    }
}
