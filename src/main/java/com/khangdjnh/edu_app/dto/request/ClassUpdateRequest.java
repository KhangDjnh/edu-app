package com.khangdjnh.edu_app.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassUpdateRequest {
    String name;
    String semester;
    String description;
}
