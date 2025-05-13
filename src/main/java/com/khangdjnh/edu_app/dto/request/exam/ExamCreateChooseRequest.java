package com.khangdjnh.edu_app.dto.request.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamCreateChooseRequest {
    @NotNull
    Long classId;
    @NotBlank
    String title;
    @NotNull
    List<Long> questionIds;
    @NotNull
    String description;
    @NotNull
    LocalDateTime startTime;
    @NotNull
    LocalDateTime endTime;
}
