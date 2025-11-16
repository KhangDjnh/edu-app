package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.ExamSubmission;
import com.khangdjnh.edu_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    List<ExamSubmission> findAllByExamId(Long examId);
    Optional<ExamSubmission> findByExamIdAndStudentId(Long examId, Long studentId);
}