package com.example.auth.repository;

import com.example.auth.entity.InvalidatedToken;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InvalidatedTokenRepository {
    private Map<String, InvalidatedToken> invalidatedTokenMap;

    @PostConstruct
    public void init() {
        invalidatedTokenMap = new HashMap<>();
    }

    public void save(InvalidatedToken invalidatedToken) {
        invalidatedTokenMap.put(invalidatedToken.getId(), invalidatedToken);
    }

    public void remove(String id) {
        invalidatedTokenMap.remove(id);
    }

    public boolean existById(String id) {
        return invalidatedTokenMap.containsKey(id);
    }
}
