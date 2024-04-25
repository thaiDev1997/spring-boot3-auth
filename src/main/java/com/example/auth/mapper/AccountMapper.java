package com.example.auth.mapper;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccount(AccountCreation accountCreation);
}
