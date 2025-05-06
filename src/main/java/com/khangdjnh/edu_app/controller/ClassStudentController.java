package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.classstudent.JoinClassByCodeRequest;
import com.khangdjnh.edu_app.dto.request.classstudent.StudentJoinClassRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.dto.response.StudentJoinClassResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.service.ClassStudentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassStudentController {
    ClassStudentService classStudentService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<StudentJoinClassResponse> createClassStudent(@RequestBody @Valid StudentJoinClassRequest request) {
        return ApiResponse.<StudentJoinClassResponse>builder()
                .message("Success")
                .code(1000)
                .result(classStudentService.createClassStudent(request))
                .build();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/students/{classId}")
    ApiResponse<List<UserResponse>> getStudentsByClassId (@PathVariable Long classId) {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Success")
                .code(1000)
                .result(classStudentService.getStudentsByClassId(classId))
                .build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/classes/{studentId}")
    ApiResponse<List<ClassResponse>> getAllClassesStudentIn (@PathVariable Long studentId) {
        return ApiResponse.<List<ClassResponse>>builder()
                .message("Success")
                .code(1000)
                .result(classStudentService.getAllClassesStudentIn(studentId))
                .build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/class-code")
    ApiResponse<StudentJoinClassResponse> joinClassStudent(@RequestBody @Valid JoinClassByCodeRequest request) {
        return ApiResponse.<StudentJoinClassResponse>builder()
                .message("Success")
                .code(1000)
                .result(classStudentService.joinClassByCode(request.getCode()))
                .build();
    }
}
