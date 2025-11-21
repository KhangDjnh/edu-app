package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassResponse {
    Long id;
    String classCode;
    String name;
    String code;
    String description;
    String semester;
    Long teacherId;
    String teacherName;
    LocalDateTime createdAt;
}
