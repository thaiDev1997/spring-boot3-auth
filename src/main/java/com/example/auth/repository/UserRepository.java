package com.example.auth.repository;

import com.example.auth.dto.response.User;
import com.example.auth.entity.InvalidatedToken;
import jakarta.annotation.PostConstruct;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepository {
    private Map<String, User> userMap;

    @PostConstruct
    public void init() {
        userMap = new HashMap<>();

        for (short i = 0; i < 100; i++) {
            String code = UUID.randomUUID().toString();
            int age = (int) (Math.random() * (50 - 18) + 18);
            userMap.put(code, new User(code, "John Doe " + i, age, LocalDate.now()));
        }
    }

    public Collection<User> getAll() {
        return userMap.values();
    }

    public User get(String code) {
        return userMap.get(code);
    }

    public User create(User user) {
        user.setCode(UUID.randomUUID().toString());
        userMap.put(user.getCode(), user);
        return user;
    }

    public void update(User user) {
        userMap.put(user.getCode(), user);
    }

    public boolean delete(String code) {
        if (userMap.containsKey(code)) {
            userMap.remove(code);
            return true;
        }
        return false;
    }
}
