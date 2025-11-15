package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationController {
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/api/confirm-email")
    public ApiResponse<String> confirmEmail(@RequestParam String token) {
        return emailConfirmationService.confirmEmail(token);
    }

}
