package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionResponse {
    Long id;
    String title;
    String content;
    LocalDateTime submittedAt;
    BigDecimal grade;
    String feedback;
    Long assignmentId;
    Long studentId;
    FileRecordResponse fileRecord;
}
