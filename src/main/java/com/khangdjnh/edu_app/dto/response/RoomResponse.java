package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import com.khangdjnh.edu_app.enums.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {

    String roomCode;

    String roomName;

    Long teacherId;

    Long classId;

    PrimarySubject subject;

    LocalDateTime startTime;

    LocalDateTime finishTime;

    String description;

    RoomStatus status = RoomStatus.STARTED;

    String classRoomPath;

    Boolean isActive = true;

    String createdBy;
}
