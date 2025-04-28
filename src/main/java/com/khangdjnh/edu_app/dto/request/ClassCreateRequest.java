package com.khangdjnh.edu_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassCreateRequest {
    @NotBlank
    String name;
    @NotBlank
    String semester;
    @NotBlank
    @Size(min = 6, max = 6, message = "code must be 6 character")
    String code;
    String description;
}
