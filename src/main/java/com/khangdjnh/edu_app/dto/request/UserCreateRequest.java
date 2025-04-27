package com.khangdjnh.edu_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    String username;
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
    @Email(message = "Email must be valid")
    String email;
    String fullName;
    LocalDate dob;
}
