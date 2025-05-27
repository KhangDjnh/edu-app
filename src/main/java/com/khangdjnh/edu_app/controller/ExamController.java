package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.exam.ExamCreateChooseRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamCreateRandomRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ExamResponse;
import com.khangdjnh.edu_app.service.ExamService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExamController {
    ExamService examService;

    @PostMapping("/random")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamResponse> createRandomExam (@RequestBody @Valid ExamCreateRandomRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .message("Success")
                .code(1000)
                .result(examService.createRandomExam(request))
                .build();
    }

    @PostMapping("/choose")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamResponse> createChooseExam (@RequestBody @Valid ExamCreateChooseRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .message("Success")
                .code(1000)
                .result(examService.createChooseExam(request))
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

    @GetMapping("/{classId}/class")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<List<ExamResponse>> getExamsByClassId (@PathVariable Long classId) {
        return ApiResponse.<List<ExamResponse>>builder()
                .message("Success")
                .code(1000)
                .result(examService.getExamsByClassId(classId))
                .build();
    }

    @GetMapping("/class/{classId}/student")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<List<ExamResponse>> getExamsInClassIdByStudent (@PathVariable Long classId) {
        return ApiResponse.<List<ExamResponse>>builder()
                .message("Success")
                .code(1000)
                .result(examService.getExamsInClassIdByStudent(classId))
                .build();
    }

    @PutMapping("/{examId}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamResponse> updateExam (@PathVariable Long examId, @RequestBody @Valid ExamUpdateRequest request) {
        return ApiResponse.<ExamResponse>builder()
                .code(1000)
                .message("Success")
                .result(examService.updateExam(examId, request))
                .build();
    }

    @PutMapping("/{examId}/start")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ExamResponse> markExamStarted (@PathVariable Long examId) {
        return ApiResponse.<ExamResponse>builder()
                .code(1000)
                .message("Success")
                .result(examService.markExamStarted(examId))
                .build();
    }

    @DeleteMapping("/{examId}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<String> deleteExam (@PathVariable Long examId) {
        examService.deleteExam(examId);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("Delete Exam Successfully")
                .build();
    }
}
