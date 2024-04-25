package com.example.auth.mapper;

import com.example.auth.dto.request.UserCreation;
import com.example.auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Spring create mapper bean within Bean container
public interface UserMapper {
    User toUser(UserCreation userCreation);
}
