package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.submission.GradeRequest;
import com.khangdjnh.edu_app.dto.request.submission.SubmissionRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.SubmissionResponse;
import com.khangdjnh.edu_app.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<SubmissionResponse> createSubmission(@ModelAttribute SubmissionRequest request) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("Success")
                .result(submissionService.createSubmission(request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ApiResponse<SubmissionResponse> getSubmission(@PathVariable Long id) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("Success")
                .result(submissionService.getSubmissionById(id))
                .build();
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<SubmissionResponse> getSubmissionsByAssignmentAndStudent(@PathVariable Long assignmentId, @PathVariable Long studentId) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("Success")
                .result(submissionService.getSubmissionsByAssignmentAndStudent(assignmentId, studentId))
                .build();
    }

    @GetMapping("/assignment/{assignmentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<SubmissionResponse>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        return ApiResponse.<List<SubmissionResponse>>builder()
                .code(1000)
                .message("Success")
                .result(submissionService.getSubmissionsByAssignment(assignmentId))
                .build();
    }

    @PutMapping("/{id}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<SubmissionResponse> gradeSubmission(@PathVariable Long id, @RequestBody GradeRequest request) {
        return ApiResponse.<SubmissionResponse>builder()
                .code(1000)
                .message("Success")
                .result(submissionService.gradeSubmission(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ApiResponse<String> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("Submission deleted successfully")
                .build();
    }
}