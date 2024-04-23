package com.example.auth.service;

import com.example.auth.dto.auth.AuthUser;
import com.example.auth.entity.Account;
import com.example.auth.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.get(username);
        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException("User name not found: " + username);
        }
        return new AuthUser(account);
    }
}
