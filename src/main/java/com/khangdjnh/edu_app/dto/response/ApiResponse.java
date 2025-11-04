package com.khangdjnh.edu_app.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    String message;
    int code;
    T result;
    @Builder.Default
    private HttpStatus httpStatus = HttpStatus.OK;
}
