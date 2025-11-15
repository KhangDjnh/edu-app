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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationController {
    private final PendingUserRepository pendingUserRepository;
    private final IdentityClient identityClient;
    private final KeycloakClientTokenService keycloakClientTokenService;
    private final UserRepository userRepository;
    private final ErrorNormalizer errorNormalizer;
    private final PasswordEncoder passwordEncoder;

    @Value("${idp.realm}")
    private String realm;

    @GetMapping("/api/confirm-email")
    @Transactional(rollbackFor = Exception.class)
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
                    realm,
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
                            .build()
                    );

            String userKeycloakId = extractUserId(creationResponse);

            assignRoleToUser("Bearer " + keycloakToken, userKeycloakId,
                    pendingUser.getRole() != null ? pendingUser.getRole().name() : "USER");

            // Lưu vào bảng users
            User user = User.builder()
                    .username(pendingUser.getUsername())
                    .email(pendingUser.getEmail())
                    .password(passwordEncoder.encode(pendingUser.getPassword()))
                    .keycloakUserId(userKeycloakId)
                    .firstName(pendingUser.getFirstName())
                    .lastName(pendingUser.getLastName())
                    .phoneNumber(pendingUser.getPhoneNumber())
                    .address(pendingUser.getAddress())
                    .gender(pendingUser.getGender())
                    .role(pendingUser.getRole())
                    .primarySubject(pendingUser.getPrimarySubject())
                    .dob(pendingUser.getDob())
                    .isActive(true)
                    .avatar("https://cdn.vectorstock.com/i/1000v/92/16/default-profile-picture-avatar-user-icon-vector-46389216.jpg")
                    .build();
            userRepository.save(user);
        } catch (FeignException exception) {
            log.error("Error while confirming email", exception);
            log.error("Error message: {}", exception.getMessage());
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

    private void assignRoleToUser(String token, String userId, String roleName) {
        ResponseEntity<Map<String, Object>> roleResponse = identityClient.getRoleByName(
                "Bearer " + token,
                realm,
                roleName
        );

        Map<String, Object> role = roleResponse.getBody();
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        identityClient.assignRoleToUser(
                "Bearer " + token,
                realm,
                userId,
                List.of(role)
        );
    }

}
