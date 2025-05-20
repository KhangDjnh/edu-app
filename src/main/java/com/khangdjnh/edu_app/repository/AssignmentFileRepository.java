package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Long> {
    List<AssignmentFile> findAllByAssignmentId(Long assignmentId);
    AssignmentFile findByIdAndIsDeletedFalse(Long assignmentFileId);
}
