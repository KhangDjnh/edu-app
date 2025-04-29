package com.khangdjnh.edu_app.dto.request.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamCreateRequest {
    @NotNull
    Long classId;
    @NotBlank
    String title;
    @NotNull
    int numberOfEasyQuestions;
    @NotNull
    int numberOfMediumQuestions;
    @NotNull
    int numberOfHardQuestions;
    @NotNull
    int numberOfVeryHardQuestions;

    String description;
    @NotNull
    LocalDateTime startTime;
    @NotNull
    LocalDateTime endTime;
}
