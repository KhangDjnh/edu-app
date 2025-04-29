package com.khangdjnh.edu_app.dto.request.user;

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
    @Size(min = 3, message = "Username must be between 3 and 30 characters")
    String avatar;
    String firstName;
    String lastName;
    LocalDate dob;
}
