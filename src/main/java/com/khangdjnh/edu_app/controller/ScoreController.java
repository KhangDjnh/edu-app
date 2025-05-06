package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ScoreResponse;
import com.khangdjnh.edu_app.service.ScoreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreController {
    ScoreService scoreService;

    @GetMapping("/{studentId}/students")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ApiResponse<List<ScoreResponse>> getAllScoreByStudentId(@PathVariable Long studentId) {
        return ApiResponse.<List<ScoreResponse>>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getAllScoreByStudentId(studentId))
                .build();
    }

    @GetMapping("/{examId}/exams")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ApiResponse<List<ScoreResponse>> getAllScoreByExamId(@PathVariable Long examId) {
        return ApiResponse.<List<ScoreResponse>>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getAllScoreByExamId(examId))
                .build();
    }

    @GetMapping("/{studentId}/students/{examId}/exams")
    @PreAuthorize("hasAnyRole('TEACHER','USER')")
    ApiResponse<ScoreResponse> getScoreByStudentIdExamId (@PathVariable Long studentId, @PathVariable Long examId) {
        return ApiResponse.<ScoreResponse>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getScoreByExamIdAndStudentId(studentId, examId))
                .build();
    }
}
