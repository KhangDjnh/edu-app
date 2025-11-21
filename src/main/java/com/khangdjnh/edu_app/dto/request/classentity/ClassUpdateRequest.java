package com.khangdjnh.edu_app.dto.request.classentity;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassUpdateRequest {
    @NotBlank
    String classCode;
    @NotBlank
    String name;
    @NotBlank
    String semester;
    String description;
}
