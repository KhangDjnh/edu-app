package com.khangdjnh.edu_app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
@Slf4j
public class WebSocketEventListener {

    // Khi client gửi CONNECT và được chấp nhận → kết nối STOMP thành công
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Unknown";
        String preferredName = "Unknown";

        if (headerAccessor.getUser() instanceof JwtAuthenticationToken jwtAuth) {
            preferredName = jwtAuth.getToken().getClaimAsString("preferred_username");
        } else if (headerAccessor.getUser() != null) {
            preferredName = headerAccessor.getUser().getName();
        }

        log.info("NEW USER JOINED ROOM! SessionId: {}, UserId: {}, Username: {}",
                sessionId, username, preferredName);
    }

    // Khi client SUBSCRIBE vào /topic/signaling/{roomId} hoặc /topic/control/{roomId}
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();

        if (destination != null && destination.contains("/topic/")) {
            String roomId = destination.substring(destination.lastIndexOf("/") + 1);
            String role = "UNKNOWN";
            String displayName = "Unknown User";

            if (headerAccessor.getUser() instanceof JwtAuthenticationToken jwtAuth) {
                displayName = jwtAuth.getToken().getClaimAsString("preferred_username");
                List<String> roles = jwtAuth.getToken().getClaimAsStringList("realm_access.roles");
                if (roles != null) {
                    if (roles.contains("TEACHER")) role = "TEACHER";
                    else if (roles.contains("STUDENT")) role = "STUDENT";
                }
            }

            log.info("USER SUBSCRIBED TO ROOM! Session: {}, RoomId: {}, User: {}. Role: {}",
                    sessionId, roomId, displayName, role);
        }
    }
}