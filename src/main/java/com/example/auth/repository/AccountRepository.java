package com.example.auth.repository;

import com.example.auth.entity.Account;
import com.example.auth.entity.Account;
import com.example.auth.enums.Permission;
import com.example.auth.enums.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class AccountRepository {
    private Map<String, Account> accountMap;

    @PostConstruct
    public void init() {
        accountMap = new HashMap<>();
        String hashedPassword = "$2a$12$QQxWOB5gRAxgztfPO2b4Tur0IdufpL.WXlq.jModkrckYQsNsar/K";
        accountMap.put("admin", new Account("admin", hashedPassword,
                new Role[]{Role.ADMIN, Role.USER}, new Permission[]{Permission.GET_ALL_USERS, Permission.GET_USER,
                Permission.CREATE_USER, Permission.DELETE_USER}));
        accountMap.put("user", new Account("user", hashedPassword,
                new Role[]{Role.USER}, new Permission[]{Permission.GET_USER}));
    }

    public Collection<Account> getAll() {
        return accountMap.values();
    }

    public Account get(String username) {
        return accountMap.get(username);
    }
}
