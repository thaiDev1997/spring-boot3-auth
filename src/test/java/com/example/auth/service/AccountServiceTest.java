package com.example.auth.service;

import static com.example.auth.enums.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Account;
import com.example.auth.entity.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.repository.AccountRepository;
import com.example.auth.repository.RoleRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(value = "/test.properties")
public class AccountServiceTest {

  @MockBean // should be @MockBean instead of @Autowired
  private AccountRepository accountRepository;

  @MockBean // should be @MockBean instead of @Autowired
  private RoleRepository roleRepository;

  @Autowired private AccountService accountService;

  private AccountCreation accountRequest;
  private Account accountResponse;

  @BeforeEach
  public void init() {
    accountRequest =
        AccountCreation.builder()
            .username("john")
            .password("test123")
            .roles(Set.of(ADMIN.name()))
            .build();
    Role role = Role.builder().name(ADMIN.name()).build();
    accountResponse =
        Account.builder()
            .username(accountRequest.getUsername()) // omit encoded_password field
            .roles(Set.of(role))
            .build();
    ;
  }

  /* Do bên trong AccountService#create có:
   + accountMapper -> thành phần stateless (INPUT thế nào thì OUTPUT thế ấy) => ko MockObject
   + accountRepository[existsByUsername - create] -> thành phần stateful, vì khi .save() có thể
      lưu dữ liệu test xuống Database) => phải MockObject bằng @MockBean
  */
  @Test
  void createAccount_validRequest_success() {
    // GIVEN (preparation)
    Mockito.when(accountRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
    Mockito.when(roleRepository.getRolesByNames(Mockito.anySet()))
        .thenReturn(Collections.EMPTY_SET);
    Mockito.when(accountRepository.save(Mockito.any())).thenReturn(accountResponse);

    // WHEN (execution on preparation)
    var response = accountService.createAccount(accountRequest);

    // THEN (expectation after execution)
    Assertions.assertThat(response.getUsername()).isEqualTo(accountResponse.getUsername());
    Assertions.assertThat(response.getRoles()).isEqualTo(accountResponse.getRoles());
    Assertions.assertThat(response.getUsername()).isEqualTo("john");
  }

  @Test
  void createAccount_accountExisted_fail() {
    // GIVEN (preparation)
    Mockito.when(accountRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

    // WHEN (execution on preparation)
    var exception =
        assertThrows(ResponseException.class, () -> accountService.createAccount(accountRequest));
    Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_EXISTED);
  }

  @Test
  @WithMockUser(username = "john") // Mock for SecurityContextHolder.getAuthentication()
  void getMyInfo_valid_success() {
    Mockito.when(accountRepository.findByUsername(Mockito.anyString()))
        .thenReturn(Optional.of(accountResponse));
    var response = accountService.getMyInfo();
    Assertions.assertThat(response.getUsername()).isEqualTo(accountResponse.getUsername());
  }

  @Test
  @WithMockUser(username = "john")
  void getMyInfo_userNotFound_error() {
    Mockito.when(accountRepository.findByUsername(Mockito.anyString()))
        .thenReturn(Optional.empty());
    var response = assertThrows(ResponseException.class, () -> accountService.getMyInfo());
    Assertions.assertThat(response.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXISTED);
  }
}
