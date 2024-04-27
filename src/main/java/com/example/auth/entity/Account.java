package com.example.auth.entity;

import com.example.auth.enums.Permission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Account {

    @Id
    String username;
    @Column(name = "encoded_password")
    String encodedPassword;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_username"),
            inverseJoinColumns = @JoinColumn(name = "roles_name"))
    Set<Role> roles;
}
