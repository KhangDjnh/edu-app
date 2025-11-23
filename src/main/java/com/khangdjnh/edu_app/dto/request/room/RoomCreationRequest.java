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
}
