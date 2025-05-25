package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAttendanceResponse {
    Integer presentNumber;
    Integer lateNumber;
    Integer absenceNumber;
    UserResponse userResponse;
}
