package com.khangdjnh.edu_app.dto.chat;

import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.enums.Emotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponseDTO {
    Long id;

    MessageUserDTO sender;

    String content;

    FileRecordResponse attachFileId;

    MessageReplyDTO replyTo;

    Emotion emotion;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
