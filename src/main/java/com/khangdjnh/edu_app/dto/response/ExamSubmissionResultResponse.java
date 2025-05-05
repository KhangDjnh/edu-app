package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.ExamSubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmissionResultResponse {
    private Long studentId;
    private String studentName;
    private BigDecimal score;
    private LocalDateTime submittedAt;
    private ExamSubmissionStatus status;
}