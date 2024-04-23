package com.example.auth.entity;

import com.example.auth.enums.Permission;
import com.example.auth.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    String username;
    String encodedPassword;
    Role[] roles;
    Permission[] permissions;
}
