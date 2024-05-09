package com.example.auth.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Account {

  @Id String username;

  @Column(name = "encoded_password")
  String encodedPassword;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "account_roles",
      joinColumns = @JoinColumn(name = "account_username"),
      inverseJoinColumns = @JoinColumn(name = "roles_name"))
  Set<Role> roles;
}
