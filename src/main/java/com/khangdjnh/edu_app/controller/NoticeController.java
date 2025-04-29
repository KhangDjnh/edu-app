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
@RequestMapping("/notices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeController {
    NoticeService noticeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    ApiResponse<MessageResponse> getNoticeById(@PathVariable Long id) {
        return ApiResponse.<MessageResponse>builder()
                .message("Success")
                .code(1000)
                .result(noticeService.getNoticeById(id))
                .build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    ApiResponse<List<MessageResponse>> getAllNoticesByUserId (@PathVariable Long userId) {
        return ApiResponse.<List<MessageResponse>>builder()
                .message("Success")
                .code(1000)
                .result(noticeService.getAllNoticesByUserId(userId))
                .build();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    ApiResponse<String> markNoticeAsRead (@PathVariable Long id) {
        noticeService.markNoticeAsRead(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Notice " + id + " marked as read")
                .build();
    }
}
