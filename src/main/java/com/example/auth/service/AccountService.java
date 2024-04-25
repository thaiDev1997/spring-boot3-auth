package com.example.auth.service;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Account;
import com.example.auth.enums.Permission;
import com.example.auth.enums.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.mapper.AccountMapper;
import com.example.auth.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        String rawPassword = "test123";
        AccountCreation admin = AccountCreation.builder().username("admin").password(rawPassword)
                .roles(new Role[]{Role.ADMIN})
                .permissions(new Permission[]{Permission.GET_ALL_USERS, Permission.GET_USER, Permission.CREATE_USER, Permission.DELETE_USER})
                .build();
        this.createAccount(admin);

        AccountCreation user = AccountCreation.builder().username("user").password(rawPassword)
                .roles(new Role[]{Role.USER})
                .permissions(new Permission[]{Permission.GET_ALL_USERS, Permission.GET_USER})
                .build();
        this.createAccount(user);
    }

    public Account createAccount(AccountCreation newAccount) {
        if (accountRepository.existsByUsername(newAccount.getUsername())) {
            throw new ResponseException(ErrorCode.ACCOUNT_EXISTED);
        }
        Account account = accountMapper.toAccount(newAccount);
        account.setEncodedPassword(passwordEncoder.encode(newAccount.getPassword()));
        return accountRepository.create(account);
    }

}
