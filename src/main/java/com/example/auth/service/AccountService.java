package com.example.auth.service;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Account;
import com.example.auth.entity.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.mapper.AccountMapper;
import com.example.auth.repository.AccountRepository;
import com.example.auth.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;


    public Account createAccount(AccountCreation newAccount) {
        if (accountRepository.existsByUsername(newAccount.getUsername())) {
            throw new ResponseException(ErrorCode.ACCOUNT_EXISTED);
        }
        Collection<String> requestRoles = newAccount.getRoles();
        Set<Role> roles;
        if (Objects.nonNull(requestRoles) || !requestRoles.isEmpty()) {
            roles = roleRepository.getRolesByNames(newAccount.getRoles());
        } else {
            roles = Collections.EMPTY_SET;
        }
        Account account = accountMapper.toAccount(newAccount);
        account.setEncodedPassword(passwordEncoder.encode(newAccount.getPassword()));
        account.setRoles(roles);
        return accountRepository.save(account);
    }
}
