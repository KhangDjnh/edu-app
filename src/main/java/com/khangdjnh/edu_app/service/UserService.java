package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.ChangePasswordRequest;
import com.khangdjnh.edu_app.dto.request.LoginRequest;
import com.khangdjnh.edu_app.dto.request.user.UserCreateRequest;
import com.khangdjnh.edu_app.dto.request.user.UserUpdateRequest;
import com.khangdjnh.edu_app.dto.response.LoginResponse;
import com.khangdjnh.edu_app.dto.response.UserResponse;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.enums.UserRole;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.exception.ErrorNormalizer;
import com.khangdjnh.edu_app.keycloak.Credential;
import com.khangdjnh.edu_app.keycloak.UserCreationParam;
import com.khangdjnh.edu_app.mapper.UserMapper;
import com.khangdjnh.edu_app.repository.IdentityClient;
import com.khangdjnh.edu_app.repository.UserRepository;
import com.khangdjnh.edu_app.util.Constants;
import com.khangdjnh.edu_app.util.StringUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;
    private final KeycloakClientTokenService keycloakClientTokenService;
    private final KeycloakUserTokenService keycloakUserTokenService;
    private final Keycloak keycloak;

    @Value("${idp.realm}")
    private String realm;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String decodedPassword = user.getPassword();
        if(!passwordEncoder.matches(request.getPassword(), decodedPassword)) {
            throw new AppException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }
        String accessToken = keycloakUserTokenService.getAccessToken(request);
        List<String> roles = extractRolesFromToken(accessToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .roles(roles)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dob(user.getDob())
                        .role(StringUtil.getStringValue(user.getRole()))
                        .address(user.getAddress())
                        .phoneNumber(user.getPhoneNumber())
                        .gender(StringUtil.getStringValue(user.getGender()))
                        .primarySubject(user.getPrimarySubject())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED_IN_DATABASE);
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED_IN_DATABASE);
        }
        try {
            var token = keycloakClientTokenService.getAccessToken();
            var creationResponse = identityClient.createUser(
                    "Bearer " + token,
                    realm,
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
            assignRoleToUser(userKeycloakId,
                    request.getRole() != null ? request.getRole().name() : "USER"
            );
            log.info("Assigned role {} to user {}", request.getRole(), userKeycloakId);
            User user = userMapper.toUser(request);
            user.setKeycloakUserId(userKeycloakId);
            user.setActive(true);
            user.setAvatar("https://www.svgrepo.com/show/452030/avatar-default.svg");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            return userMapper.toUserResponse(user);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeycloakException(exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(String commonSearch, String role) {

        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by("createdAt").descending()
        );

        UserRole userRole = null;
        if (role != null && !role.isBlank()) {
            userRole = UserRole.valueOf(role.toUpperCase());
        }

        Page<User> userPage = userRepository.searchUsers(
                (commonSearch == null || commonSearch.isBlank()) ? null : commonSearch,
                userRole,
                pageable
        );

        return userPage.map(userMapper::toUserResponse);
    }


    @PreAuthorize( "hasRole('ADMIN')")
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toUserResponse)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getMyInfo(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var user = userRepository.findByKeycloakUserId(userKeycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @Transactional(rollbackFor = Exception.class)
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
                realm,
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

    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUserFromRequest(user, request);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
    }

    private List<String> extractRolesFromToken(String token) {
        try {
            List<String> result = new ArrayList<>();
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Lấy roles từ realm_access
            Map<String, Object> realmAccess = (Map<String, Object>) claims.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> allRoles = (List<String>) realmAccess.get("roles");
                allRoles.forEach(role -> {
                    if(Constants.ACTOR_ROLES.contains(role)){
                        result.add(role);
                    }
                });
                return result;
            }

            return List.of();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
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
