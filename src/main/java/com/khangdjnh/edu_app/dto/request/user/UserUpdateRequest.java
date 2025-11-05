package com.khangdjnh.edu_app.dto.request.user;

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
public class UserUpdateRequest {
    @Size(min = 3, max = 40, message = "Username must be between 3 and 40 characters")
    String username;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    String gender;
    String role;
    LocalDate dob;
}
