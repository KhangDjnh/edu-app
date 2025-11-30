package com.khangdjnh.edu_app.dto.comment;

import com.khangdjnh.edu_app.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentReplyResponse {
    Long id;

    User userComment;

    String content;

}
