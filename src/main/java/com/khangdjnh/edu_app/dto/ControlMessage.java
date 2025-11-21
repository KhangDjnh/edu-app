package com.khangdjnh.edu_app.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControlMessage {
    String type;        // "CHAT", "RAISE_HAND", "MUTE", "GRANT_SPEAK", "KICK"
    String fromUser;
    String text;        // cho chat
    Instant timestamp;
}
