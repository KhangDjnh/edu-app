package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    long count();
}
