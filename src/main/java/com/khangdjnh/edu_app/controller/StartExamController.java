package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ExamSubmissionResultResponse;
import com.khangdjnh.edu_app.dto.response.StartExamResponse;
import com.khangdjnh.edu_app.service.ExamResultService;
import com.khangdjnh.edu_app.service.ExamSubmissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartExamController {
    ExamSubmissionService examSubmissionService;
    ExamResultService examResultService;

    @PostMapping("/{examId}/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<StartExamResponse> startExam(@PathVariable Long examId) {
        return ApiResponse.<StartExamResponse>builder()
                .message("Success")
                .code(1000)
                .result(examSubmissionService.startExam(examId))
                .build();
    }


    @PostMapping("/{submissionId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<String> submitExam(@PathVariable Long submissionId) {
        examSubmissionService.submitExam(submissionId);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Exam submitted")
                .build();
    }

    @GetMapping("/submission/{submissionId}/result")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ApiResponse<BigDecimal> getScore(@PathVariable Long submissionId) {
        return ApiResponse.<BigDecimal>builder()
                .code(1000)
                .message("Success")
                .result(examSubmissionService.getScoreBySubmissionId(submissionId))
                .build();
    }

    @GetMapping("/{examId}/results")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ApiResponse<List<ExamSubmissionResultResponse>> getExamResults(@PathVariable Long examId) {
        return ApiResponse.<List<ExamSubmissionResultResponse>>builder()
                .message("Results fetched")
                .code(1000)
                .result(examResultService.getResults(examId))
                .build();
    }
}
