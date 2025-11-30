package com.khangdjnh.edu_app.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreationRequest {
    @NotNull
    Long poster;

    @NotNull
    Long classId;

    @NotNull
    Long postTypeId;

    @NotNull
    String postTitle;

    @NotNull
    String postContent;

    MultipartFile file;

    String postIcon;

    String postBackground;
}
