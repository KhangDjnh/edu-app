package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByClassEntityId(Long classId);
    List<LeaveRequest> findByStudent_IdAndClassEntityId(Long studentId, Long classId);

    @Override
    Optional<LeaveRequest> findById(Long id);
}
