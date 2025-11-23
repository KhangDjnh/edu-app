package com.khangdjnh.edu_app.mapper;

import com.khangdjnh.edu_app.dto.request.room.RoomCreationRequest;
import com.khangdjnh.edu_app.dto.response.RoomResponse;
import com.khangdjnh.edu_app.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomCreationRequest request);

    RoomResponse toRoomResponse(Room room);
}
