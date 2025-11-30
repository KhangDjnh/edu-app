package com.khangdjnh.edu_app.dto.chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageReplyDTO {
    Long id;

    MessageUserDTO sender;

    String content;
}
