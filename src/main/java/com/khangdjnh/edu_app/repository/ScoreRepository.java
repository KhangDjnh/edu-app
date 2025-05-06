package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByStudentId(Long studentId);
    List<Score> findAllByExamId(Long examId);
    Optional<Score> findByStudentIdAndExamId(Long studentId, Long examId);
}
