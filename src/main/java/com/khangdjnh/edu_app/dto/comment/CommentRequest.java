package com.khangdjnh.edu_app.dto.comment;

import com.khangdjnh.edu_app.enums.Emotion;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRequest {
    @NotNull
    Long postId;

    @NotNull
    Long userId;

    @NotNull
    String content;

    Long replyTo;

    Emotion emotion;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
