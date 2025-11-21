package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.dto.ICEServer;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JoinResponse {
    String signalingUrl;
    String roomId;
    Long userId;
    String userRole;
    String token;
    List<ICEServer> iceServers;
    Integer maxParticipants;
    Map<String, Object> metadata;
}
