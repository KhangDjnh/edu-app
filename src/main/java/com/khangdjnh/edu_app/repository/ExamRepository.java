package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findAllByClassEntityId(Long classId);
}
