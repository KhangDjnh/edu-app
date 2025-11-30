package com.khangdjnh.edu_app.dto.chat;

import com.khangdjnh.edu_app.enums.Emotion;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageCreationRequest {

    @NotNull
    Long sender;

    @NotNull
    Long conversationId;

    @NotNull
    String content;

    MultipartFile file;

    Long replyTo;

    Emotion emotion;
}
