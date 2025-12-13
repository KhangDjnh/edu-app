package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearningRoadmapResponse {
    Long id;
    String title;
    String description;
    FileRecordResponse fileRecord;
    ClassResponse classResponse;
    String backgroundImage;
    String iconImage;
    List<LearningRoadmapResponse> children;
    Integer roadmapIndex;
    String createdBy;
    LocalDateTime createdAt;
}
