package com.example.auth.service;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Account;
import com.example.auth.entity.Role;
import com.example.auth.exception.ErrorCode;
import com.example.auth.exception.ResponseException;
import com.example.auth.repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.example.auth.enums.Role.ADMIN;

@SpringBootTest
public class AccountServiceTest {

    @MockBean // should be @MockBean instead of @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private AccountCreation accountRequest;
    private Account accountResponse;

    @BeforeEach
    public void init() {
        accountRequest = AccountCreation.builder()
                .username("john")
                .password("test123")
                .roles(Set.of(ADMIN.name()))
                .build();
        Role role = Role.builder().name(ADMIN.name()).build();
        accountResponse = Account.builder()
                .username(accountRequest.getUsername()) // omit encoded_password field
                .roles(Set.of(role))
                .build();;
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
        var exception = assertThrows(ResponseException.class, () -> accountService.createAccount(accountRequest));
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_EXISTED);
    }
}
