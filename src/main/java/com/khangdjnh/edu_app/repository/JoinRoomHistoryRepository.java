package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.JoinRoomHistory;
import com.khangdjnh.edu_app.enums.JoinRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinRoomHistoryRepository extends JpaRepository<JoinRoomHistory, Long> {
    JoinRoomHistory findTopByUserIdAndRoomIdOrderByIdDesc(Long userId, Long roomId);
    List<JoinRoomHistory> findByRoomIdAndJoinRoomStatus(Long roomId, JoinRoomStatus joinRoomStatus);
}
