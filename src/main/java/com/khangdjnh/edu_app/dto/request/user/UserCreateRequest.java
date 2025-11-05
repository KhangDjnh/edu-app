package com.khangdjnh.edu_app.dto.request.user;

import com.khangdjnh.edu_app.enums.Gender;
import com.khangdjnh.edu_app.enums.PrimarySubject;
import com.khangdjnh.edu_app.enums.UserRole;
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
    @Size(min = 3, max = 40, message = "Username must be between 3 and 40 characters")
    String username;
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
    @Email(message = "Email must be valid")
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    Gender gender;
    PrimarySubject primarySubject;
    UserRole role;
    LocalDate dob;
}
