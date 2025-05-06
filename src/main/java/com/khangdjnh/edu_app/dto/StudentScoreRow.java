package com.khangdjnh.edu_app.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentScoreRow {
    Long studentId;
    String fullName;
    LocalDate dob;
    Map<Long, BigDecimal> scores;
    BigDecimal averageScore;
}
