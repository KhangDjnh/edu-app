package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.LearningRoadmapRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.LearningRoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-roadmaps")
@RequiredArgsConstructor
public class LearningRoadmapController {
    private final LearningRoadmapService learningRoadmapService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> createLearningRoadmap(@ModelAttribute @Valid LearningRoadmapRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Learning roadmap created successfully")
                .result(learningRoadmapService.createLearningRoadmap(request))
                .build();
    }

    @GetMapping("/class/{classId}")
    public ApiResponse<?> getAllLearningRoadmapsInClass(@PathVariable Long classId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(learningRoadmapService.getAllLearningRoadmapsInClass(classId))
                .build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> updateLearningRoadmap(
            @PathVariable Long id,
            @ModelAttribute @Valid LearningRoadmapRequest request
    ) {
        return ApiResponse.builder()
                .code(1000)
                .message("Learning roadmap updated successfully")
                .result(learningRoadmapService.updateLearningRoadmap(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteLearningRoadmap(@PathVariable Long id) {
        learningRoadmapService.deleteLearningRoadmap(id);
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result("Learning roadmap deleted successfully")
                .build();
    }
}
