package com.khangdjnh.edu_app.dto.request.attendance;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceByDateRequest {
    Long classId;
    LocalDate attendanceDate;
}
