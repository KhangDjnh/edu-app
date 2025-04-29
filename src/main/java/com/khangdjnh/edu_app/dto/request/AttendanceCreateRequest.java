package com.khangdjnh.edu_app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceCreateRequest {
    @NotNull
    Long classId;

    @NotNull
    LocalDate attendanceDate;

    @NotNull
    @Size(min = 1, message = "Attendance list must not be empty")
    List<AttendanceStudentRequest> attendances;

}
