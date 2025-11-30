package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.message.MessageResponse;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.NoticeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeController {
    NoticeService noticeService;

    @GetMapping("/{userId}/count")
    @PreAuthorize("hasAnyRole('USER', 'TEACHER', 'STUDENT')")
    ApiResponse<?> getUnReadNoticeCount(@PathVariable Long userId) {
        return ApiResponse.builder()
                .code(1000)
                .message("Success")
                .result(noticeService.getAllUnreadNoticeCount(userId))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'TEACHER', 'STUDENT')")
    ApiResponse<MessageResponse> getNoticeById(@PathVariable Long id) {
        return ApiResponse.<MessageResponse>builder()
                .message("Success")
                .code(1000)
                .result(noticeService.getNoticeById(id))
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'TEACHER', 'STUDENT')")
    ApiResponse<List<MessageResponse>> getAllNoticesByUserId (@PathVariable Long userId) {
        return ApiResponse.<List<MessageResponse>>builder()
                .message("Success")
                .code(1000)
                .result(noticeService.getAllNoticesByUserId(userId))
                .build();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('USER', 'TEACHER', 'STUDENT')")
    ApiResponse<String> markNoticeAsRead (@PathVariable Long id) {
        noticeService.markNoticeAsRead(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Notice " + id + " marked as read")
                .build();
    }
}
