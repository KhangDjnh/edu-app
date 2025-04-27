package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.ChangePasswordRequest;
import com.khangdjnh.edu_app.dto.request.LoginRequest;
import com.khangdjnh.edu_app.dto.request.UserCreateRequest;
import com.khangdjnh.edu_app.dto.request.UserUpdateRequest;
import com.khangdjnh.edu_app.dto.response.LoginResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.exception.ErrorNormalizer;
import com.khangdjnh.edu_app.keycloak.Credential;
import com.khangdjnh.edu_app.keycloak.UserCreationParam;
import com.khangdjnh.edu_app.mapper.UserMapper;
import com.khangdjnh.edu_app.repository.IdentityClient;
import com.khangdjnh.edu_app.repository.UserRepository;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    IdentityClient identityClient;
    ErrorNormalizer errorNormalizer;
    KeycloakClientTokenService keycloakClientTokenService;
    KeycloakUserTokenService keycloakUserTokenService;
    PendingUserService pendingUserService;


    private String extractUserId(ResponseEntity<?> responseEntity) {
        String location = Objects.requireNonNull(responseEntity.getHeaders().get("Location")).getFirst();
        String[] splittedString = location.split("/");
        return splittedString[splittedString.length - 1];
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String decodedPassword = user.getPassword();
        if(!passwordEncoder.matches(request.getPassword(), decodedPassword)) {
            throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }
        return LoginResponse.builder()
                .accessToken(keycloakUserTokenService.getAccessToken(request))
                .build();
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED_IN_DATABASE);
        }
        try {
            var token = keycloakClientTokenService.getAccessToken();
            var creationResponse = identityClient.createUser(
                    "Bearer " + token,
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .value(request.getPassword())
                                    .temporary(false)
                                    .build()))
                            .build());
            //Khi goi toi api createUser de tao User tren Keycloak thi tra ve Header.Location co chua userId, ta can lay no dua vao db
            String userKeycloakId = extractUserId(creationResponse);
            User user = userMapper.toUser(request);
            user.setKeycloakUserId(userKeycloakId);
            user.setActive(true);
            user.setAvatar("https://cdn.vectorstock.com/i/1000v/92/16/default-profile-picture-avatar-user-icon-vector-46389216.jpg");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            return userMapper.toUserResponse(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeycloakException(exception);
        }
    }
    @PreAuthorize( "hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }
    @PreAuthorize( "hasRole('ADMIN')")
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserResponse).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    @PreAuthorize("hasRole('USER')")
    public UserResponse getMyInfo(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var user = userRepository.findByKeycloakUserId(userKeycloakId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
    public void changePassword(Long id, ChangePasswordRequest request) {
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String userKeycloakId = user.getKeycloakUserId();
        String oldEncodedPassword = user.getPassword();
        if(!passwordEncoder.matches(request.getOldPassword(), oldEncodedPassword)) {
            throw new AppException(ErrorCode.OLD_PASSWORD_IS_INCORRECT);
        }
        String accessToken = keycloakClientTokenService.getAccessToken();

        identityClient.resetUserPassword(
                "Bearer " + accessToken,
                "education-service",
                userKeycloakId,
                Credential.builder()
                        .type("password")
                        .value(request.getNewPassword())
                        .temporary(false)
                        .build()
        );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUserFromRequest(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
    }
}
