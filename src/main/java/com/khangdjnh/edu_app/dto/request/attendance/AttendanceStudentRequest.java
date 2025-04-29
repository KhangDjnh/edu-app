package com.khangdjnh.edu_app.dto.request.attendance;

import com.khangdjnh.edu_app.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceStudentRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private AttendanceStatus status;

}