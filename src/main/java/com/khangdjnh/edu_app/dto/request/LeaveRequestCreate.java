package com.khangdjnh.edu_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaveRequestCreate {
    @NotNull
    Long classId;
    @NotBlank
    String reason;
    @NotNull
    LocalDate leaveDate;
}
