package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.post.EmotionCreationRequest;
import com.khangdjnh.edu_app.dto.post.PostCreationRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> createPost(@ModelAttribute @Valid PostCreationRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/class")
    public ApiResponse<?> getAllPosts(@RequestParam Long classId){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(postService.getAll(classId))
                .build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getPostById(@PathVariable Long postId){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(postService.getPost(postId))
                .build();
    }

    @PutMapping(value = "/update/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> updatePost(@PathVariable Long postId, @ModelAttribute @Valid PostCreationRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(postService.updatePost(postId, request))
                .build();
    }

    @PostMapping("/emotion")
    public ApiResponse<?> createEmotion(@RequestBody @Valid EmotionCreationRequest request){
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(postService.emotionalPost(request.getPostId(), request.getUserId(), request.getEmotion()))
                .build();
    }

    @DeleteMapping(value = "/{postId}")
    public ApiResponse<?> deletePost(@PathVariable Long postId){
        postService.deletePost(postId);
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result("Delete Post successfully")
                .build();
    }
}
