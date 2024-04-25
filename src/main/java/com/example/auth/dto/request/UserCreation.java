package com.example.auth.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreation {
    @Size(min = 4, max = 256, message = "the name length must be between {min} and {max}")
    String name;
    @Min(value = 18)
    int age;
    LocalDate dateOfBirth;
}
