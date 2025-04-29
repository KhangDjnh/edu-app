package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClassEntityIdAndAttendanceDate(Long classId, LocalDate attendanceDate);
    List<Attendance> findByStudentIdAndClassEntity_Id(Long studentId, Long classId);
}
