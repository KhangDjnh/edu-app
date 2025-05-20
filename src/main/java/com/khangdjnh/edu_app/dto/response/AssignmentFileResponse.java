package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentFileResponse {
    String fileName;
    String fileType;
    String filePath;
    String downloadUrl;
    long fileSize;
    LocalDateTime uploadedAt;
}
