package com.khangdjnh.edu_app.dto.message;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationMessage {
    String content;
    Long receiverId;
}