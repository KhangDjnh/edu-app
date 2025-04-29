package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.attendance.AttendanceCreateRequest;
import com.khangdjnh.edu_app.dto.request.attendance.AttendanceUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.AttendanceCreateResponse;
import com.khangdjnh.edu_app.dto.response.AttendanceResponse;
import com.khangdjnh.edu_app.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {
    AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<AttendanceCreateResponse> createClassAttendance (@RequestBody @Valid AttendanceCreateRequest request) {
        return ApiResponse.<AttendanceCreateResponse>builder()
                .message("Success")
                .code(1000)
                .result(attendanceService.createClassAttendance(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<AttendanceResponse> updateAttendance (@RequestBody @Valid AttendanceUpdateRequest request, @PathVariable Long id) {
        return ApiResponse.<AttendanceResponse>builder()
                .message("Success")
                .code(1000)
                .result(attendanceService.updateAttendance(request, id))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        return ApiResponse.<AttendanceResponse>builder()
                .message("Success")
                .code(1000)
                .result(attendanceService.getAttendanceById(id))
                .build();
    }

    @GetMapping("/class")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<List<AttendanceResponse>> getAllAttendanceInClass (@RequestParam Long classId, @RequestParam LocalDate attendanceDate) {
        return ApiResponse.<List<AttendanceResponse>>builder()
                .message("Success")
                .code(1000)
                .result(attendanceService.getAllAttendanceInClass(classId, attendanceDate))
                .build();
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    ApiResponse<List<AttendanceResponse>> getAllAttendanceInClassAndStudent (@RequestParam Long studentId, @RequestParam Long classId) {
        return ApiResponse.<List<AttendanceResponse>>builder()
                .message("Success")
                .code(1000)
                .result(attendanceService.getAllAttendanceInClassAndStudent(studentId, classId))
                .build();
    }

}
