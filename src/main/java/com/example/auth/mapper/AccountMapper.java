package com.example.auth.mapper;

import com.example.auth.dto.request.AccountCreation;
import com.example.auth.dto.response.AccountResponse;
import com.example.auth.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

  @Mapping(target = "roles", ignore = true)
  Account toAccount(AccountCreation accountCreation);

  AccountResponse toAccountResponse(Account account);
}
