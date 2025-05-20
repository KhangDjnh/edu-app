package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.assignments.AssignmentCreateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.AssignmentResponse;
import com.khangdjnh.edu_app.service.AssignmentFileService;
import com.khangdjnh.edu_app.service.AssignmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
}
