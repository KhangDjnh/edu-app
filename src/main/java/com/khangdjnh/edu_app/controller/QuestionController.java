package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.question.QuestionCreateRequest;
import com.khangdjnh.edu_app.dto.request.question.QuestionSearchRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.QuestionDetailResponse;
import com.khangdjnh.edu_app.dto.response.QuestionResponse;
import com.khangdjnh.edu_app.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QuestionController {
    QuestionService examQuestionService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<QuestionResponse> createQuestion(@RequestBody @Valid QuestionCreateRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.createQuestion(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @RequestBody @Valid QuestionCreateRequest request
    ) {
        return ApiResponse.<QuestionResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.updateQuestion(id, request))
                .build();
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Page<QuestionResponse>> searchQuestions(
            @RequestBody QuestionSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.<Page<QuestionResponse>>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.searchQuestions(request, pageable))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<QuestionDetailResponse> getQuestionById(@PathVariable Long id) {
        return ApiResponse.<QuestionDetailResponse>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.getQuestionById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<Page<QuestionDetailResponse>> getAllQuestionInClass(
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<Page<QuestionDetailResponse>>builder()
                .message("Success")
                .code(1000)
                .result(examQuestionService.getAllQuestionByClass(classId, page, size))
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
