package com.example.auth.controller;

import com.example.auth.dto.response.AccountResponse;
import com.example.auth.service.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountController {

    AccountService accountService;

    @GetMapping(value = "/my-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountResponse getMyInfo() {
        return accountService.getMyInfo();
    }

}
