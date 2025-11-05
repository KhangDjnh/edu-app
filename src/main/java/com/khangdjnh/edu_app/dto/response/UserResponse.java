package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    String gender;
    String role;
    PrimarySubject primarySubject;
    String avatar;
    LocalDate dob;
}