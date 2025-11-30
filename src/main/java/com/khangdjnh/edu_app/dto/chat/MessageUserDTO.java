package com.khangdjnh.edu_app.dto.chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageUserDTO {
    Long id;
    String username;
    String email;
    String firstName;
    String lastName;
    String avatar;
}
