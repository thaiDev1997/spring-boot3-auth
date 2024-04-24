package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserService {

    UserRepository userRepository;

    @PreAuthorize(value = "hasRole('ADMIN') AND hasAuthority('GET_ALL_USERS')") // Role: ADMIN - Permission: GET_ALL_USERS
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @PreAuthorize(value = "hasAuthority('GET_USER')") // Permission: GET_USER
    public User get(String code) {
        return userRepository.get(code);
    }

    @PreAuthorize(value = "hasAuthority('CREATE_USER')") // Permission: CREATE_USER
    public User create(User user) {
        log.info("UserService#create");
        return userRepository.create(user);
    }

    @PreAuthorize(value = "hasAuthority('UPDATE_USER')") // Permission: UPDATE_USER
    public void update(User user) {
        userRepository.update(user);
    }

    @PreAuthorize(value = "hasAuthority('DELETE_USER')") // Permission: DELETE_USER
    public boolean delete(String code) {
        return userRepository.delete(code);
    }

}
