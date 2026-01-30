package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.ClassType;
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
    String name;
    String code;
    String description;
    String semester;
    Long teacherId;
    String teacherName;
    ClassType classType;
    String powerBy;
    String classIntroduction;
    LocalDateTime createdAt;
}
