package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import com.khangdjnh.edu_app.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    String id;
    String roomCode;
    String roomName;
    Long teacherId;
    Long classId;
    PrimarySubject subject;
    LocalDateTime startTime;
    String description;
    RoomStatus status;
    LocalDateTime createdAt;
    String createdBy;
}
