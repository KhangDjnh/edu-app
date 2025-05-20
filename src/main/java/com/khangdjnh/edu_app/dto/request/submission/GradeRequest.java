package com.khangdjnh.edu_app.dto.request.submission;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeRequest {
    BigDecimal grade;
    String feedback;
}
