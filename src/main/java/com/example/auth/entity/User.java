package com.example.auth.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    String code;
    @Size(min = 4, max = 256, message = "the name length must be between {min} and {max}")
    String name;
    @Min(value = 18)
    int age;
    LocalDate dateOfBirth;
}
