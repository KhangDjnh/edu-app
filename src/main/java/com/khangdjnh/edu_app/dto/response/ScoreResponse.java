package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScoreResponse {
    Long studentId;
    Long classId;
    Long ExamId;
    BigDecimal score;

}
