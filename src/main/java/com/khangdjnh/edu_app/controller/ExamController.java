package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.exam.ExamCreateRequest;
import com.khangdjnh.edu_app.dto.request.examquestion.ExamQuestionCreateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ExamResponse;
import com.khangdjnh.edu_app.service.ExamService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExamController {
    ExamService examService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamResponse> createRandomExam (@RequestBody @Valid ExamCreateRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .message("Success")
                .code(1000)
                .result(examService.createRandomExam(request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    ApiResponse<ExamResponse> getExamById (@PathVariable Long id) {
        return ApiResponse.<ExamResponse>builder()
                .message("Success")
                .code(1000)
                .result(examService.getExamById(id))
                .build();
    }
}
