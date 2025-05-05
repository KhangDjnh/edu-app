package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    Long id;
    Long classId;
    String question;
    AnswerOption answer;
    QuestionLevel level;
}
