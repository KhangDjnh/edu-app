package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.entity.ExamAnswer;
import com.khangdjnh.edu_app.enums.ExamSubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamResponse {
    private Long submissionId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamSubmissionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private BigDecimal score;
    private List<ExamAnswer> listExamAnswers;
}