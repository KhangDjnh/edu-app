package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionFileResponse {
    Long id;
    String fileName;
    String filePath;
    String fileType;
    Long fileSize;
    LocalDateTime uploadedAt;
}
