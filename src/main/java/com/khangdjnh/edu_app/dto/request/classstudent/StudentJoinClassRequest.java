package com.khangdjnh.edu_app.dto.request.classstudent;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentJoinClassRequest {
    @NotNull
    @Positive
    Long classId;

    @NotNull
    @Positive
    Long studentId;
}
