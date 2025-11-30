package com.khangdjnh.edu_app.dto.comment;

import com.khangdjnh.edu_app.dto.post.PostResponse;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.Emotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;

    PostResponse post;

    User userComment;

    String content;

    CommentReplyResponse replyTo;

    Emotion emotion;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
