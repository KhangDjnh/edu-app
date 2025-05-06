package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.question.SubmitAnswerRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.ExamAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam-submissions")
public class ExamAnswerController {
    private final ExamAnswerService examAnswerService;

    @PostMapping("/{submissionId}/answers")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<String> submitAnswer(@PathVariable Long submissionId, @RequestBody @Valid SubmitAnswerRequest request) {
        examAnswerService.submitAnswer(submissionId, request);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Answer submitted")
                .build();
    }
}
