package com.khangdjnh.edu_app.dto.request.submission;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionRequest {
    Long  studentId;
    Long assignmentId;
    String title;
    String content;
    List<MultipartFile> files = new ArrayList<>();
}
