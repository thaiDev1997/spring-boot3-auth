package com.example.auth.repository;

import com.example.auth.entity.Account;
import com.example.auth.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class AccountRepository {
    private Map<String, Account> accountMap = new HashMap<>();

    @Autowired
    private AccountMapper accountMapper;

    public Collection<Account> getAll() {
        return accountMap.values();
    }

    public Account get(String username) {
        return accountMap.get(username);
    }

    public boolean existsByUsername(String username) {
        if (Objects.isNull(username)) {
            return false;
        }
        return accountMap.containsKey(username);
    }

    public Account create(Account newAccount) {
        if (Objects.isNull(newAccount)) {
            return null;
        }
        accountMap.put(newAccount.getUsername(), newAccount);
        return newAccount;
    }
}
