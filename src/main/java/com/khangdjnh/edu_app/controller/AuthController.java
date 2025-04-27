package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.LoginRequest;
import com.khangdjnh.edu_app.dto.request.UserCreateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.LoginResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.service.PendingUserService;
import com.khangdjnh.edu_app.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
    UserService userService;
    PendingUserService pendingUserService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.<LoginResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.login(request))
                .build();
    }
    @PostMapping("/register")
    ApiResponse<String> register(@RequestBody @Valid UserCreateRequest request) {
        pendingUserService.createUser(request);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("Confirm email to complete registration")
                .build();
    }

}