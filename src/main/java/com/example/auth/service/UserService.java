package com.example.auth.service;

import com.example.auth.dto.response.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    Map<String, User> userMap;

    @PostConstruct
    public void init() {
        userMap = new HashMap<>();
        for (short i = 0; i < 100; i++) {
            String code = UUID.randomUUID().toString();
            int age = (int) (Math.random() * (50 - 18) + 18);
            userMap.put(code, new User(code, "John Doe " + i, age, LocalDate.now()));
        }

    }

    @PreAuthorize(value = "hasRole('ADMIN') AND hasAuthority('GET_ALL_USERS')") // Role: ADMIN - Permission: GET_ALL_USERS
    public Collection<User> getAll() {
        return userMap.values();
    }

    @PreAuthorize(value = "hasAuthority('GET_USER')") // Permission: GET_USER
    public User get(String code) {
        return userMap.get(code);
    }

    @PreAuthorize(value = "hasAuthority('CREATE_USER')") // Permission: CREATE_USER
    public User create(User user) {
        user.setCode(UUID.randomUUID().toString());
        userMap.put(user.getCode(), user);
        return user;
    }

    @PreAuthorize(value = "hasAuthority('UPDATE_USER')") // Permission: UPDATE_USER
    public void update(User user) {
        userMap.put(user.getCode(), user);
    }

    @PreAuthorize(value = "hasAuthority('DELETE_USER')") // Permission: DELETE_USER
    public boolean delete(String code) {
        if (userMap.containsKey(code)) {
            userMap.remove(code);
            return true;
        }
        return false;
    }

}
