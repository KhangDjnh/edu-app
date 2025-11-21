package com.khangdjnh.edu_app.dto.request;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateRoomRequest {
    String roomName;
    Long teacherId;
    Long classId;
    PrimarySubject subject;
    LocalDateTime startTime;
    String description;
}
