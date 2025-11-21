package com.khangdjnh.edu_app.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalingMessage {
    String type;        // "offer", "answer", "ice-candidate"
    String sdp;         // cho offer/answer
    Object candidate;   // cho ice
    String fromSession;
}
