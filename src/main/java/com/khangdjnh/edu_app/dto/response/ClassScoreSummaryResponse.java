package com.khangdjnh.edu_app.dto.response;

import com.khangdjnh.edu_app.dto.ExamInfo;
import com.khangdjnh.edu_app.dto.StudentScoreRow;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassScoreSummaryResponse {
    Long classId;
    List<ExamInfo> exams;
    List<StudentScoreRow> students;
}
