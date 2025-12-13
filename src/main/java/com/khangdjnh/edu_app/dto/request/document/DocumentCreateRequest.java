package com.khangdjnh.edu_app.dto.request.document;

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
public class DocumentCreateRequest {
    @NotNull(message = "classId không được null")
    Long classId;
    @NotBlank(message = "title không được blank")
    String title;

    String filePath;

    @NotNull(message = "file không được để trống")
    MultipartFile file;
}
