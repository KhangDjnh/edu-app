package com.khangdjnh.edu_app.dto.request.room;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomCreationRequest {
    @NotNull
    String roomName;

    @NotNull
    Long teacherId;

    @NotNull
    Long classId;

    @NotNull
    PrimarySubject subject;

    @NotNull
    LocalDateTime startTime;

    String description;

    public RoomCreationRequest(Long classId, Long teacherId, String roomName, PrimarySubject subject, LocalDateTime startTime, String description) {
        this.classId = classId;
        this.teacherId = teacherId;
        this.roomName = roomName;
        this.subject = subject;
        this.startTime = startTime;
        this.description = description;
    }
}
