package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.LeaveRequest;
import com.khangdjnh.edu_app.enums.LeaveRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByClassEntityIdOrderByRequestedAtDesc(Long classId);
    List<LeaveRequest> findByStudent_IdAndClassEntityId(Long studentId, Long classId);
    List<LeaveRequest> findByClassEntity_IdAndLeaveDateAndStatus(Long classId, LocalDate date, LeaveRequestStatus status);
    @Override
    Optional<LeaveRequest> findById(Long id);
}
