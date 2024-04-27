package com.example.auth.controller;

import com.example.auth.dto.request.AuthenticationRequest;
import com.example.auth.dto.request.UserCreation;
import com.example.auth.entity.User;
import com.example.auth.exception.ErrorCode;
import com.example.auth.service.AuthenticationService;
import com.example.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    private UserCreation userRequest;
    private User userResponse;
    private String token;

    @BeforeEach
    public void init() {
        var dob = LocalDate.of(1998, 1, 1);
        var age = LocalDate.now().getYear() - dob.getYear();
        userRequest = UserCreation.builder()
                .name("John Doe")
                .age(age)
                .dateOfBirth(dob)
                .build();
        userResponse = User.builder()
                .code(UUID.randomUUID().toString())
                .name(userRequest.getName())
                .age(age)
                .dateOfBirth(dob)
                .build();
        token = OAuth2AccessToken.TokenType.BEARER.getValue() + " " + authenticationService.generateAdminTokenTestScope();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN (preparation)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userRequest);
        // WHEN (execution on preparation)
        Mockito.when(userService.create(ArgumentMatchers.any())).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content))
                // THEN (expectation after execution)
                .andExpect(MockMvcResultMatchers.status().isOk())
                // jsonPath -> result.id or response.data, ...
                .andExpect(MockMvcResultMatchers.jsonPath("code").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("code").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("code").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(userResponse.getCode()));
    }

    @Test
    void createUser_invalidName_fail() throws Exception {
        // GIVEN
        String invalidName = "joh";
        userRequest.setName(invalidName);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(userRequest);
        // WHEN
        Mockito.when(userService.create(ArgumentMatchers.any())).thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(content))
                // THEN
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                // jsonPath -> result.id or response.data, ...
                .andExpect(MockMvcResultMatchers.jsonPath("code").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("code").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(ErrorCode.INVALID_REQUEST.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("result.name").value(invalidName));
    }
}
