package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(
            value = "SELECT q.* FROM exam_exam_questions eq " +
                    "JOIN exam_questions q ON eq.question_id = q.id " +
                    "WHERE eq.exam_id = :examId",
            countQuery = "SELECT count(*) FROM exam_exam_questions WHERE exam_id = :examId",
            nativeQuery = true)
    Page<Question> findQuestionsByExamId(@Param("examId") Long examId, Pageable pageable);

    List<Question> findByLevel(QuestionLevel level);
    List<Question> findByClassEntityId(Long classId);
}
