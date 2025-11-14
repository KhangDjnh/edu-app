package com.khangdjnh.edu_app.dto.message;

import com.khangdjnh.edu_app.enums.NoticeType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    Long id;
    String content;
    LocalDateTime createAt;
    Boolean read;
    NoticeType type;
    String senderUserName;
}
