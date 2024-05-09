package com.example.auth.service;

import com.example.auth.dto.auth.AuthUser;
import com.example.auth.entity.Account;
import com.example.auth.repository.AccountRepository;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Account> accountOptional = accountRepository.findByUsername(username);
    if (accountOptional.isEmpty()) {
      throw new UsernameNotFoundException("User name not found: " + username);
    }
    return new AuthUser(accountOptional.get());
  }
}
