package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.comment.CommentRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public ApiResponse<?> createComment(@RequestBody @Valid CommentRequest request) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(commentService.createComment(request))
                .build();
    }

    @GetMapping("/{commentId}")
    public ApiResponse<?> getComment(@PathVariable Long commentId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(commentService.getComment(commentId))
                .build();
    }

    @GetMapping("post/{postId}")
    public ApiResponse<?> getAllCommentsByPost(@PathVariable Long postId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(commentService.getAllComment(postId))
                .build();
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result("Delete comment successfully")
                .build();
    }
}
