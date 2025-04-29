package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.examquestion.ExamQuestionCreateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ExamQuestionResponse;
import com.khangdjnh.edu_app.service.ExamQuestionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExamQuestionController {
    ExamQuestionService examQuestionService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamQuestionResponse> createQuestion(@RequestBody @Valid ExamQuestionCreateRequest request) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.createQuestion(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamQuestionResponse> updateQuestion(
            @PathVariable Long id,
            @RequestBody @Valid ExamQuestionCreateRequest request
    ) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.updateQuestion(id, request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamQuestionResponse> getQuestionById(@PathVariable Long id) {
        return ApiResponse.<ExamQuestionResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.getQuestionById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<List<ExamQuestionResponse>> getAllQuestion(@RequestParam Long classId) {
        return ApiResponse.<List<ExamQuestionResponse>>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.getAllQuestionByClass(classId))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<String> deleteQuestion(@PathVariable Long id) {
        examQuestionService.deleteQuestion(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Question " + id + " deleted successfully")
                .build();
    }

}
