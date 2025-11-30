package com.khangdjnh.edu_app.dto.post;

import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.PostType;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.Emotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long id;

    User poster;

    ClassResponse classResponse;

    PostType postType;

    String postTitle;

    String postContent;

    FileRecordResponse attachFile;

    String postIcon;

    String postBackground;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Map<Emotion, Long> emotionCounter;

    int commentCount;
}
