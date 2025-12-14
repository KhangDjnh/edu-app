package com.khangdjnh.edu_app.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkExamStartResponse {
    Long id;
    String title;
    String description;
    Long classId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime createdAt;
    RoomResponse room;
}
