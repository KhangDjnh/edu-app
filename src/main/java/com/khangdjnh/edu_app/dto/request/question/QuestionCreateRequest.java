package com.khangdjnh.edu_app.dto.request.question;

import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreateRequest {
    @NotNull
    Long classId;
    @NotBlank
    String question;
    @NotBlank
    String optionA;
    @NotBlank
    String optionB;
    @NotBlank
    String optionC;
    @NotBlank
    String optionD;
    @NotNull(message = "Đáp án không được để trống")
    AnswerOption answer;
    @NotNull
    QuestionLevel level;
    @NotNull
    Integer chapter;
}
