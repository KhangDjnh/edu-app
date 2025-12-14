package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.AnswerOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ExamAnswerResponse {

    Long questionId;

    AnswerOption selectedOption;

}
