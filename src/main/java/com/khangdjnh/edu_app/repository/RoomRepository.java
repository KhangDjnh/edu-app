package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.Room;
import com.khangdjnh.edu_app.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    long count();
    List<Room> findByClassIdAndStatusAndExam(Long classId, RoomStatus status, Exam exam);
    Room findByExamId(Long examId);
}
