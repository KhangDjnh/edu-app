package com.khangdjnh.edu_app.service;

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
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConfirmationService {
    private final PendingUserRepository pendingUserRepository;
    private final IdentityClient identityClient;
    private final KeycloakClientTokenService keycloakClientTokenService;
    private final UserRepository userRepository;
    private final ErrorNormalizer errorNormalizer;
    private final PasswordEncoder passwordEncoder;
    private final Keycloak keycloak;


    @Value("${idp.realm}")
    private String realm;

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
            log.info("Created user in Keycloak with ID: {}", userKeycloakId);
            assignRoleToUser(userKeycloakId,
                    pendingUser.getRole() != null ? pendingUser.getRole().name() : "USER"
            );
            log.info("Assigned role {} to user {}", pendingUser.getRole(), userKeycloakId);
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

    private void assignRoleToUser(String userKeycloakId, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        List<RoleRepresentation> allRealmRoles = realmResource.roles().list();

        RoleRepresentation roleToAssign = allRealmRoles.stream()
                .filter(r -> r.getName().equals(roleName))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_ROLE));
        // Lấy user
        UserResource userResource = realmResource.users().get(userKeycloakId);

        // Gán role realm-level
        userResource.roles().realmLevel().add(Collections.singletonList(roleToAssign));
    }

}
