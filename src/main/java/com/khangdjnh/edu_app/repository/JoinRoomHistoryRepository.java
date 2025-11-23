package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.JoinRoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinRoomHistoryRepository extends JpaRepository<JoinRoomHistory, Long> {
    JoinRoomHistory findTopByUserIdAndRoomIdOrderByIdDesc(Long userId, Long roomId);
}
