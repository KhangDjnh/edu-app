package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.ExamQuestion;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByLevel(QuestionLevel level);
    List<ExamQuestion> findByClassEntityId(Long classId);
}
