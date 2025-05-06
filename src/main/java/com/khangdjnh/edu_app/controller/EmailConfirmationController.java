package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.entity.PendingUser;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.exception.ErrorNormalizer;
import com.khangdjnh.edu_app.keycloak.Credential;
import com.khangdjnh.edu_app.keycloak.UserCreationParam;
import com.khangdjnh.edu_app.repository.IdentityClient;
import com.khangdjnh.edu_app.repository.PendingUserRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import com.khangdjnh.edu_app.service.KeycloakClientTokenService;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailConfirmationController {
    PendingUserRepository pendingUserRepository;
    IdentityClient identityClient;
    KeycloakClientTokenService keycloakClientTokenService;
    UserRepository userRepository;
    ErrorNormalizer errorNormalizer;
    PasswordEncoder passwordEncoder;

    @GetMapping("/api/confirm-email")
    public ApiResponse<String> confirmEmail(@RequestParam String token) {
        PendingUser pendingUser = pendingUserRepository.findByConfirmationToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.CONFIRM_MAIL_TOKEN_IS_INVALID_OR_EXPIRED));

        if (pendingUser.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.CONFIRM_MAIL_TOKEN_IS_EXPIRED);
        }

        // Gọi keycloak để tạo tài khoản
        try {
            var keycloakToken = keycloakClientTokenService.getAccessToken();
            var creationResponse = identityClient.createUser(
                    "Bearer " + keycloakToken,
                    UserCreationParam.builder()
                            .username(pendingUser.getUsername())
                            .email(pendingUser.getEmail())
                            .firstName(pendingUser.getFirstName())
                            .lastName(pendingUser.getLastName())
                            .enabled(true)
                            .emailVerified(true)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .value(pendingUser.getPassword())
                                    .temporary(false)
                                    .build()))
                            .build());

            String userKeycloakId = extractUserId(creationResponse);

            // Lưu vào bảng users
            User user = User.builder()
                    .username(pendingUser.getUsername())
                    .email(pendingUser.getEmail())
                    .password(passwordEncoder.encode(pendingUser.getPassword()))
                    .keycloakUserId(userKeycloakId)
                    .firstName(pendingUser.getFirstName())
                    .lastName(pendingUser.getLastName())
                    .dob(pendingUser.getDob())
                    .isActive(true)
                    .avatar("https://cdn.vectorstock.com/i/1000v/92/16/default-profile-picture-avatar-user-icon-vector-46389216.jpg")
                    .build();
            userRepository.save(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeycloakException(exception);
        }
        // Xoá pending user
        pendingUserRepository.delete(pendingUser);

        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Email confirmed")
                .build();
    }

    private String extractUserId(ResponseEntity<?> responseEntity) {
        String location = Objects.requireNonNull(responseEntity.getHeaders().get("Location")).getFirst();
        String[] splittedString = location.split("/");
        return splittedString[splittedString.length - 1];
    }
}
