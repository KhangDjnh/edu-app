package com.khangdjnh.edu_app.dto.request.document;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentCreateRequest {
    Long classId;
    @NotBlank
    String title;
    @NotBlank
    String filePath;
}
