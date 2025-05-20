package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {
}
