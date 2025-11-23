package com.khangdjnh.edu_app.dto.request.room;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JoinRoomCreation {

    @NotNull
    Long userId;

    @NotNull
    Long roomId;
}
