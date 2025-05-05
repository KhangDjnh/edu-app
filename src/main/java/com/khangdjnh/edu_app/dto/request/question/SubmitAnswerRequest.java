package com.khangdjnh.edu_app.dto.request.question;

import com.khangdjnh.edu_app.enums.AnswerOption;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitAnswerRequest {
    @NotNull
    Long questionId;
    @NotNull
    AnswerOption answerOption;
}
