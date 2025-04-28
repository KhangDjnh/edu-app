package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentJoinClassResponse {
    Long classId;
    User student;
}
