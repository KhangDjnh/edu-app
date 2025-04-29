package com.khangdjnh.edu_app.controller;


import com.khangdjnh.edu_app.dto.request.LeaveRequestCreate;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.LeaveRequestResponse;
import com.khangdjnh.edu_app.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-request")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LeaveRequestController {
    LeaveRequestService leaveRequestService;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<LeaveRequestResponse> createLeaveRequest (@RequestBody @Valid LeaveRequestCreate request) {
        return ApiResponse.<LeaveRequestResponse>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.createLeaveRequest(request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<LeaveRequestResponse> getLeaveRequestById(@PathVariable Long id) {
        return ApiResponse.<LeaveRequestResponse>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.getLeaveRequestById(id))
                .build();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<LeaveRequestResponse> approveLeaveRequest (@PathVariable Long id) {
        return ApiResponse.<LeaveRequestResponse>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.approveLeaveRequest(id))
                .build();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<LeaveRequestResponse> rejectLeaveRequest (@PathVariable Long id) {
        return ApiResponse.<LeaveRequestResponse>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.rejectLeaveRequest(id))
                .build();
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<List<LeaveRequestResponse>> getAllLeaveRequestInClass (@PathVariable Long classId) {
        return ApiResponse.<List<LeaveRequestResponse>>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.getAllLeaveRequestInClass(classId))
                .build();
    }
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<List<LeaveRequestResponse>> getAllLeaveRequestStudent (@PathVariable Long studentId) {
        return ApiResponse.<List<LeaveRequestResponse>>builder()
                .message("Success")
                .code(1000)
                .result(leaveRequestService.getAllLeaveRequestStudent(studentId))
                .build();
    }
}
