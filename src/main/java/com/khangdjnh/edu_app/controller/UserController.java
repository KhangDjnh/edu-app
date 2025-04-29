package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.ChangePasswordRequest;
import com.khangdjnh.edu_app.dto.request.UserCreateRequest;
import com.khangdjnh.edu_app.dto.request.UserUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        log.info("request: {}", request);
        return ApiResponse.<UserResponse>builder()
                .message("Success")
                .code(1000)
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers () {
        return ApiResponse.<List<UserResponse>>builder()
                .code(1000)
                .message("Success")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById (@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.getUserById(id))
                .build();
    }
    @GetMapping("/getUserInfo")
    ApiResponse<UserResponse> getUserInfo () {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isOwner(#id)")
    ApiResponse<UserResponse> updateUser (@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .code(1000)
                .message("Success")
                .result(userService.updateUser(id, request))
                .build();
    }

    @PutMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @authz.isOwner(#id)")
    ApiResponse<String> changePassword (@PathVariable Long id, @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("Password changed successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteUser (@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Success")
                .result("User deleted successfully")
                .build();
    }

}
