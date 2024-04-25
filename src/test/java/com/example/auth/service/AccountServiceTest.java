package com.example.auth.service;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.dto.request.AuthenticationRequest;
import com.example.auth.entity.Account;
import com.example.auth.enums.Permission;
import com.example.auth.enums.Role;
import com.example.auth.repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Arrays;

@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private AccountCreation accountRequest;
    private Account accountResponse;
    private String token;

    @BeforeEach
    public void init() {
        accountRequest = AccountCreation.builder()
                .username("john")
                .password("test123")
                .roles(new Role[]{Role.ADMIN})
                .permissions(new Permission[]{Permission.GET_ALL_USERS, Permission.GET_USER, Permission.CREATE_USER, Permission.DELETE_USER})
                .build();
        accountResponse = Account.builder()
                .username(accountRequest.getUsername()) // omit encoded_password field
                .roles(accountRequest.getRoles())
                .permissions(accountRequest.getPermissions())
                .build();;
        token = OAuth2AccessToken.TokenType.BEARER.getValue() + " " + authenticationService.authenticate(AuthenticationRequest.builder()
                .username("admin")
                .password("test123")
                .build());
    }

    /* Do bên trong AccountService#create có:
     + accountMapper -> thành phần stateless (INPUT thế nào thì OUTPUT thế ấy) => ko MockObject
     + accountRepository[existsByUsername - create] -> thành phần stateful (khi .save() có thể lưu dữ liệu test xuống Database) => phải MockObject
    */
    @Test
    void createAccountAccount_validRequest_success() {
        // GIVEN (preparation)
        Mockito.when(accountRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        Mockito.when(accountRepository.create(Mockito.any())).thenReturn(accountResponse);

        // WHEN (execution on preparation)
        var response = accountService.createAccount(accountRequest);

        // THEN (expectation after execution)
        Assertions.assertThat(response.getUsername().equals(accountResponse.getUsername()));
        Assertions.assertThat(Arrays.equals(response.getRoles(), accountResponse.getRoles()));
    }

}
