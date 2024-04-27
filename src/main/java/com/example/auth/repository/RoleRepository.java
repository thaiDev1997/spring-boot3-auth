package com.example.auth.repository;

import com.example.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    @Query(value = "SELECT role FROM Role role WHERE role.name IN (:names)")
    Set<Role> getRolesByNames(@Param(value = "names") Collection<String> names);
}
