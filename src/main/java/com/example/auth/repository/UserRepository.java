package com.example.auth.repository;

import com.example.auth.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {
    private Map<String, User> userMap = new HashMap<>();

    public Collection<User> getAll() {
        return userMap.values();
    }

    public User get(String code) {
        return userMap.get(code);
    }

    public User create(User user) {
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
