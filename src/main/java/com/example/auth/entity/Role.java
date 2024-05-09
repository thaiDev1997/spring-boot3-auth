package com.example.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role {
  @Id String name;
  String description;

  @ManyToMany Set<Permission> permissions;

  public void addPermissions(Collection<Permission> permissions) {
    if (Objects.isNull(this.permissions)) {
      this.permissions = new HashSet<>();
    }
    this.permissions.addAll(permissions);
  }

  public void addPermission(Permission permission) {
    this.addPermissions(Collections.singleton(permission));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return Objects.equals(name, role.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
