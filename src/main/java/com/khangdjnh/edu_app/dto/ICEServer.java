package com.khangdjnh.edu_app.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ICEServer {
    String urls;
    String username;
    String credential;

    public ICEServer(String urls) {
        this.urls = urls;
    }
}

