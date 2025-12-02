package com.khangdjnh.edu_app.dto.chat;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationMessageResponse {
    Long conversationId;
    MessageUserDTO sender;
    MessageUserDTO receiver;
    LocalDateTime updatedAt;
    List<MessageResponseDTO> listMessages;
}
