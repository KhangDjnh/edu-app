package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.enums.LeaveRequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestResponse {
    Long id;
    Long studentId;
    String studentName;
    Long classId;
    LocalDate leaveDate;
    String reason;
    LeaveRequestStatus status;
}
