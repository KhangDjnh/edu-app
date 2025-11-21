package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Room;
import com.khangdjnh.edu_app.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    int countByClassId(Long classId);
    Room findByRoomCode(String roomCode);
    List<Room> findByClassIdAndStatus(Long classId, RoomStatus status);
}
