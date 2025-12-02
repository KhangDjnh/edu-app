package com.khangdjnh.edu_app.dto.post;

import com.khangdjnh.edu_app.enums.Emotion;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmotionCreationRequest {
    Long postId;
    Long userId;
    Emotion emotion;
}
