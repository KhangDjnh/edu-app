package com.khangdjnh.edu_app.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("Success")
                .code(200)
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers () {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Success")
                .code(200)
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById (@RequestParam Long id) {
        return ApiResponse.<UserResponse>builder()
                .message("Success")
                .code(200)
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUserById (@RequestParam Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .message("Success")
                .code(200)
                .result(userService.updateUserById(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteUserById (@RequestParam Long id) {
        userService.deleteUserById(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(200)
                .result("User deleted successfully")
                .build();
    }

}
