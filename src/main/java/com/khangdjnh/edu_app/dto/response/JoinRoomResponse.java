package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JoinRoomResponse {
    String username;

    String userEmail;

    String roomName;

    String roomCode;

    LocalDateTime joinedAt;

    LocalDateTime leftAt;
}
