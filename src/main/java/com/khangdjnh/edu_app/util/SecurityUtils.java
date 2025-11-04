package com.khangdjnh.edu_app.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class SecurityUtils {
    public static String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            // Trường này thường là "preferred_username" trong token của Keycloak
            return jwt.getClaimAsString("preferred_username");
        }

        return null;
    }
}
