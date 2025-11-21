package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.ControlMessage;
import com.khangdjnh.edu_app.dto.SignalingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
@Slf4j
public class ClassroomWebSocketController {
    @MessageMapping("/signaling/{roomId}")
    @SendTo("/topic/signaling/{roomId}")
    public SignalingMessage relaySignaling(
            @DestinationVariable String roomId,
            SignalingMessage message,
            @Header("simpSessionId") String sessionId
    ) {
        // Có thể log, validate, hoặc lưu lịch sử
        log.info("Room {}: Received signaling message", roomId);
        log.info("Relay signaling message from session {}: {}", sessionId, message);
        message.setFromSession(sessionId);
        return message;
    }

    @MessageMapping("/control/{roomId}")
    @SendTo("/topic/control/{roomId}")
    public ControlMessage relayControl(
            @DestinationVariable String roomId,
            ControlMessage message,
            @Header("simpSessionId") String sessionId,
            Principal principal
    ) {
        log.info("Room {}: Received control message", roomId);
        log.info("simpSessionId: {}", sessionId);
        String currentUser = principal.getName();
        // hoặc nếu muốn lấy preferred_username:
        // String currentUser = ((org.springframework.security.oauth2.jwt.Jwt)
        //     ((org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken) principal)
        //     .getPrincipal()
        // ).getClaimAsString("preferred_username");

        message.setFromUser(currentUser);
        message.setTimestamp(Instant.now());
        return message;
    }
}
