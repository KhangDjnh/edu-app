package com.khangdjnh.edu_app.dto.request.question;

import com.khangdjnh.edu_app.enums.QuestionLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionSearchRequest {
    String keyword;
    Long classId;
    Integer chapter;
    QuestionLevel level;
}
