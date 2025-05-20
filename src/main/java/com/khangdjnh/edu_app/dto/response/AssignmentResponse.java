package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentResponse {
    Long id;
    String title;
    String content;
    Long classId;
    List<AssignmentFileResponse> files;
    LocalDateTime startAt;
    LocalDateTime endAt;
    LocalDateTime createdAt;
}
