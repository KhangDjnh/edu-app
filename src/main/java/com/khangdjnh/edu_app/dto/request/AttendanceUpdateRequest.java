package com.khangdjnh.edu_app.dto.request;


import com.khangdjnh.edu_app.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequest {

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private AttendanceStatus status;

}