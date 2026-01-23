package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/question-analysis/exam/{examId}")
    public ApiResponse<?> getQuestionAnalysisByExamId(@PathVariable Long examId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(dashboardService.getQuestionAnalysisReport(examId))
                .build();
    }

    @GetMapping("/score-distribution/exam/{examId}")
    public ApiResponse<?> getScoreDistributionByExamId(@PathVariable Long examId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(dashboardService.getScoreDistributionReport(examId))
                .build();
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<?> getLearningDashboardByClassId(@PathVariable Long classId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(dashboardService.getLearningDashboardByClassId(classId))
                .build();
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<?> getLearningDashboardByStudentId(@PathVariable Long studentId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(dashboardService.getLearningDashboardByStudentId(studentId))
                .build();
    }
}
