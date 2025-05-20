package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.assignments.AssignmentCreateRequest;
import com.khangdjnh.edu_app.dto.request.assignments.AssignmentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.AssignmentResponse;
import com.khangdjnh.edu_app.service.AssignmentFileService;
import com.khangdjnh.edu_app.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentController {
    AssignmentService assignmentService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<AssignmentResponse> createAssignment(
            @ModelAttribute AssignmentCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        request.setFiles(files);
        log.info("Files received: " + request.getFiles());
        log.info("Files count: " + (request.getFiles() != null ? request.getFiles().size() : 0));
        return ApiResponse.<AssignmentResponse>builder()
                .message("Success")
                .code(1000)
                .result(assignmentService.createAssignment(request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    public ApiResponse<AssignmentResponse> getAssignmentById(@PathVariable Long id) {
        return ApiResponse.<AssignmentResponse>builder()
                .code(1000)
                .message("Success")
                .result(assignmentService.getAssignmentById(id))
                .build();
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<Page<AssignmentResponse>> getAssignmentsForStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<Page<AssignmentResponse>>builder()
                .message("Success")
                .code(1000)
                .result(assignmentService.getAssignmentsForStudent(studentId, classId, keyword, startFrom, endTo, page, size))
                .build();
    }


    @GetMapping("/{classId}/class")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ApiResponse<List<AssignmentResponse>> getAssignmentsByClass(@PathVariable Long classId) {
        return ApiResponse.<List<AssignmentResponse>>builder()
                .message("Success")
                .code(1000)
                .result(assignmentService.getAllAssignmentsByClassId(classId))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<AssignmentResponse> updateAssignment(@RequestBody @Valid AssignmentUpdateRequest request, @PathVariable Long id) throws IOException {
        return ApiResponse.<AssignmentResponse>builder()
                .code(1000)
                .message("Success")
                .result(assignmentService.updateAssignment(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<String> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignmentById(id);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("Delete Assignment Successfully")
                .build();
    }

}
