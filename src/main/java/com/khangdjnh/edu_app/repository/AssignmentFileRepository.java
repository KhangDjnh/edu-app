package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Long> {
}
