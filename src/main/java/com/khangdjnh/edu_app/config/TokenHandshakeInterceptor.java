package com.khangdjnh.edu_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class TokenHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/education-service/protocol/openid-connect/certs").build();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("Start to handshake!");
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token != null && !token.isBlank()) {
                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    List<String> realmRoles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                            .map(m -> (List<String>) m.get("roles"))
                            .orElse(List.of());

                    Set<String> ignoredRoles = Set.of("offline_access", "default-roles-education-service", "uma_authorization");

                    List<String> filteredRoles = realmRoles.stream()
                            .filter(r -> !ignoredRoles.contains(r))
                            .toList();

                    List<GrantedAuthority> authorities = filteredRoles.stream()
                            .map(r -> "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .map(a -> (GrantedAuthority) a)
                            .toList();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    jwt.getSubject(),
                                    null,
                                    authorities
                            );

                    attributes.put("user", auth);
                    log.info("attributes: {}", attributes);
                    log.info("Handshake successfully!");
                    return true;
                } catch (Exception e) {
                    log.info("Handshake failed!");
                    log.error(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        log.info("After handshake successfully!");
    }
}