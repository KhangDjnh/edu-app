package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceResponse {
    Long id;
    Long studentId;
    Long classId;
    LocalDate attendanceDate;
    AttendanceStatus status;
}
