package com.khangdjnh.edu_app.dto.response;


import com.khangdjnh.edu_app.enums.ExamSubmissionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentExamResponse {
    Long id;
    String title;
    String description;
    Long classId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime createdAt;
    ExamSubmissionStatus status;
}
