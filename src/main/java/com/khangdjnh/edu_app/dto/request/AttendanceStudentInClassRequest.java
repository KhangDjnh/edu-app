package com.khangdjnh.edu_app.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceStudentInClassRequest {
    Long classId;
    Long studentId;
}
