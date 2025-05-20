package com.khangdjnh.edu_app.util;

import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationHelper {
    private final UserRepository userRepository;
    public boolean isOwner(Long id) {
        User currentUser = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;

        // Giả sử bạn dùng Keycloak, token là KeycloakPrincipal -> lấy sub
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            String sub = jwt.getClaimAsString("sub");
            return sub.equals(currentUser.getKeycloakUserId());
        }

        return false;
    }
}