package com.example.auth.configuration;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Permission;
import com.example.auth.entity.Role;
import com.example.auth.exception.ResponseException;
import com.example.auth.repository.PermissionRepository;
import com.example.auth.repository.RoleRepository;
import com.example.auth.service.AccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.example.auth.enums.Permission.*;
import static com.example.auth.enums.Role.ADMIN;
import static com.example.auth.enums.Role.USER;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    AccountService accountService;

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "datasource.driver-class-name", havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner() {
        log.info("Init application.....");
        return args -> {
            // ADMIN
            Role adminRole = Role.builder().name(ADMIN.name()).description("Administrator").build();
            // USER
            Role userRole = Role.builder().name(USER.name()).description("User").build();

            // Permissions
            Permission getAllUsers = Permission.builder().name(GET_ALL_USERS.name()).build();
            Permission getUser = Permission.builder().name(GET_USER.name()).build();
            Permission createUser = Permission.builder().name(CREATE_USER.name()).build();
            Permission editUser = Permission.builder().name(EDIT_USER.name()).build();
            Permission deleteUser = Permission.builder().name(DELETE_USER.name()).build();

            Collection<Permission> allPermissions = List.of(getAllUsers, getUser, createUser, editUser, deleteUser);
            permissionRepository.saveAll(allPermissions);
            adminRole.addPermissions(allPermissions);
            roleRepository.save(adminRole);
            userRole.addPermission(getUser);
            roleRepository.save(userRole);

            String rawPassword = "test123";
            try {
                accountService.createAccount(AccountCreation.builder().username("admin").password(rawPassword).roles(Set.of(ADMIN.name())).build());
                accountService.createAccount(AccountCreation.builder().username("user").password(rawPassword).roles(Set.of(USER.name())).build());
            } catch (ResponseException ignoredException) {
            }
        };
    }

    @Bean
    CustomBeanPostProcessor customBeanPostProcessor() {
        return new CustomBeanPostProcessor();
    }
}
