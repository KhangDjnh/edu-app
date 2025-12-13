package com.khangdjnh.edu_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LearningRoadmapRequest {
    @NotNull(message = "classId must not be null")
    Long classId;
    @NotBlank(message = "title must not be blank")
    String title;

    String description;

    MultipartFile file;

    @NotBlank(message = "backgroundImage must not be blank")
    String backgroundImage;

    @NotBlank(message = "iconImage must not be blank")
    String iconImage;

    Long parentId;
}
