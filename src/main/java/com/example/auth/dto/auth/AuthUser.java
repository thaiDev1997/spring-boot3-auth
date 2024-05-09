package com.example.auth.dto.auth;

import com.example.auth.entity.Account;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthUser implements UserDetails {
  Account account;

  public AuthUser(Account account) {
    this.account = account;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return account.getEncodedPassword();
  }

  @Override
  public String getUsername() {
    return account.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
