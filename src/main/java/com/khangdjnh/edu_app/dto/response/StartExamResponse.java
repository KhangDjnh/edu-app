package com.khangdjnh.edu_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamResponse {
    private Long submissionId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}