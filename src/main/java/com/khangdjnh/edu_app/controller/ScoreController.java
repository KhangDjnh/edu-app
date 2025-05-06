package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ClassScoreSummaryResponse;
import com.khangdjnh.edu_app.dto.response.ScoreResponse;
import com.khangdjnh.edu_app.service.ExcelExportService;
import com.khangdjnh.edu_app.service.ScoreService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreController {
    ScoreService scoreService;
    ExcelExportService excelExportService;

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

    @GetMapping("/classes")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ApiResponse<List<ScoreResponse>> getAllScoreByClassIdExamId (
            @RequestParam Long classId,
            @RequestParam Long examId
    ) {
        return ApiResponse.<List<ScoreResponse>>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getAllScoreByClassIdExamId(classId, examId))
                .build();
    }

    @GetMapping("/exams")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    ApiResponse<ScoreResponse> getScoreByStudentIdExamId (
            @RequestParam Long studentId,
            @RequestParam Long examId
    ) {
        return ApiResponse.<ScoreResponse>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getScoreByExamIdAndStudentId(studentId, examId))
                .build();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('TEACHER')")
    public ApiResponse<ClassScoreSummaryResponse> getScoreSummaryByClassId(@RequestParam Long classId) {
        return ApiResponse.<ClassScoreSummaryResponse>builder()
                .message("Success")
                .code(1000)
                .result(scoreService.getScoreSummaryByClassId(classId))
                .build();
    }

    @GetMapping("/classes/{classId}/scores/export")
    public void exportClassScores(@PathVariable Long classId, HttpServletResponse response) throws IOException, IOException {
        ClassScoreSummaryResponse summary = scoreService.getScoreSummaryByClassId(classId);
        excelExportService.exportScoreToExcel(summary, response);
    }

}
