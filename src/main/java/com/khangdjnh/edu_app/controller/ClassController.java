package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.classentity.ClassCreateRequest;
import com.khangdjnh.edu_app.dto.request.classentity.ClassUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.ClassResponse;
import com.khangdjnh.edu_app.service.ClassService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassController {
    ClassService classService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ClassResponse> createClass (@RequestBody @Valid ClassCreateRequest request) {
        return ApiResponse.<ClassResponse>builder()
                .message("Success")
                .code(1000)
                .result(classService.createClass(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ClassResponse>> getAllClasses() {
        return ApiResponse.<List<ClassResponse>>builder()
                .message("Success")
                .code(1000)
                .result(classService.getAllClasses())
                .build();
    }

    @GetMapping("/suggested")
    ApiResponse<?> getSuggestedClasses() {
        return ApiResponse.builder()
                .message("Success")
                .code(1000)
                .result(classService.getSuggestedClasses())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<ClassResponse> getClassById(@PathVariable Long id) {
        return ApiResponse.<ClassResponse>builder()
                .message("Success")
                .code(1000)
                .result(classService.getClassById(id))
                .build();
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<List<ClassResponse>> getAllClassesByTeacherId() {
        return ApiResponse.<List<ClassResponse>>builder()
                .message("Success")
                .code(1000)
                .result(classService.getClassByTeacher())
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<ClassResponse> updateClassById(@PathVariable Long id,@RequestBody @Valid ClassUpdateRequest request) {
        return ApiResponse.<ClassResponse>builder()
                .message("Success")
                .code(1000)
                .result(classService.updateClassById(id, request))
                .build();
    }

    @GetMapping("/search")
    ApiResponse<List<ClassResponse>> searchClassesByKeyword(@RequestParam String keyword) {
        return ApiResponse.<List<ClassResponse>>builder()
                .message("Success")
                .code(1000)
                .result(classService.searchClassesByKeyword(keyword))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<String> deleteClassById(@PathVariable Long id) {
        classService.deleteClassById(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Class " + id + " successfully deleted")
                .build();
    }

}
