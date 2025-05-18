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
public class QuestionDetailResponse {
    Long id;
    Long classId;
    Integer chapter;
    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    AnswerOption answer;
    QuestionLevel level;
}
