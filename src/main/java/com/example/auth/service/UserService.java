package com.example.auth.service;

import com.example.auth.dto.request.UserCreation;
import com.example.auth.entity.User;
import com.example.auth.mapper.UserMapper;
import com.example.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserService {
  @Value("${spring.datasource.driver-class-name}")
  private String datasourceDriverClassName;

  final UserRepository userRepository;
  final UserMapper userMapper;

  @PostConstruct
  public void init() {
    if ("org.postgresql.Driver".equals(datasourceDriverClassName)) {
      userRepository.deleteAll();
      for (short i = 0; i < 50; i++) {
        int age = (int) (Math.random() * (50 - 18) + 18);
        this.create(
            UserCreation.builder()
                .name("John Doe " + i)
                .age(age)
                .dateOfBirth(LocalDate.now())
                .build());
      }
    }
  }

  @PreAuthorize(
      value = "hasRole('ADMIN') AND hasAuthority('GET_ALL_USERS')") // Role: ADMIN - Permission:
  // GET_ALL_USERS
  public Collection<User> getAll() {
    return userRepository.findAll();
  }

  @PreAuthorize(value = "hasAuthority('GET_USER')") // Permission: GET_USER
  public User get(String code) {
    return userRepository.findById(code).orElse(null);
  }

  @PreAuthorize(value = "hasAuthority('CREATE_USER')") // Permission: CREATE_USER
  public User create(UserCreation newUser) {
    User user = userMapper.toUser(newUser);
    user.setCode(UUID.randomUUID().toString());
    return userRepository.save(user);
  }

  @PreAuthorize(value = "hasAuthority('UPDATE_USER')") // Permission: UPDATE_USER
  public void update(User user) {
    userRepository.save(user);
  }

  @PreAuthorize(value = "hasAuthority('DELETE_USER')") // Permission: DELETE_USER
  public boolean delete(String code) {
    User user = userRepository.findById(code).orElse(null);
    if (Objects.nonNull(user)) {
      userRepository.delete(user);
      return true;
    }
    return false;
  }
}
