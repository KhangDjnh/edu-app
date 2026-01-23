package com.khangdjnh.edu_app.repository.Impl;

import com.khangdjnh.edu_app.dto.QuestionAnalysisDTO;
import com.khangdjnh.edu_app.dto.ScoreDistributionDTO;
import com.khangdjnh.edu_app.dto.StudentLearningDashboardDTO;
import com.khangdjnh.edu_app.repository.DashboardRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<QuestionAnalysisDTO> getAnalysisByExamId(Long examId) {
        String sql = "SELECT * FROM view_exam_question_analysis WHERE exam_id = ?";
        return jdbcTemplate.query(sql, rowMapper, examId);
    }

    @Override
    public List<ScoreDistributionDTO> getScoreDistributionByExamId(Long examId) {
        String sql = "SELECT * FROM view_exam_score_distribution WHERE exam_id = ?";
        return jdbcTemplate.query(sql, scoreMapper, examId);
    }

    @Override
    public List<StudentLearningDashboardDTO> getLearningDashboardByClassId(Long classId) {
        String sql = "SELECT * FROM view_student_learning_dashboard WHERE class_id = ?";
        return jdbcTemplate.query(sql, dashboardMapper, classId);
    }

    @Override
    public List<StudentLearningDashboardDTO> getLearningDashboardByStudentId(Long studentId) {
        String sql = "SELECT * FROM view_student_learning_dashboard WHERE student_id = ?";
        return jdbcTemplate.query(sql, dashboardMapper, studentId);
    }

    // Mapper để chuyển từng dòng ResultSet sang DTO
    private final RowMapper<QuestionAnalysisDTO> rowMapper = (rs, rowNum) -> new QuestionAnalysisDTO(
            rs.getLong("exam_id"),
            rs.getString("exam_title"),
            rs.getLong("question_id"),
            rs.getString("question_content"),
            rs.getString("correct_answer"),
            rs.getString("difficulty_level"),
            rs.getLong("total_responses"),
            rs.getLong("total_correct"),
            rs.getLong("total_incorrect"),
            rs.getDouble("error_rate"),
            rs.getString("most_common_wrong_answer")
    );

    // RowMapper để chuyển đổi từng dòng ResultSet sang Record
    private final RowMapper<ScoreDistributionDTO> scoreMapper = (rs, rowNum) -> new ScoreDistributionDTO(
            rs.getLong("exam_id"),
            rs.getString("exam_title"),
            rs.getString("class_name"),
            rs.getDouble("score"),
            rs.getString("score_range"),
            rs.getTimestamp("started_at") != null ? rs.getTimestamp("started_at").toLocalDateTime() : null,
            rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toLocalDateTime() : null,
            rs.getLong("time_spent_minutes")
    );

    private final RowMapper<StudentLearningDashboardDTO> dashboardMapper = (rs, rowNum) -> new StudentLearningDashboardDTO(
            rs.getLong("student_id"),
            rs.getString("student_name"),
            rs.getString("student_email"),
            rs.getString("student_avatar"),
            rs.getLong("class_id"),
            rs.getString("class_name"),
            rs.getString("semester"),
            rs.getInt("present_number"),
            rs.getInt("absence_number"),
            rs.getInt("late_number"),
            rs.getDouble("attendance_rate"),
            rs.getObject("avg_exam_score") != null ? rs.getDouble("avg_exam_score") : null,
            rs.getLong("exams_completed"),
            rs.getObject("avg_assignment_grade") != null ? rs.getDouble("avg_assignment_grade") : null,
            rs.getLong("assignments_submitted"),
            rs.getLong("total_assignments_in_class"),
            rs.getString("assignment_completion_ratio"),
            rs.getDouble("overall_score")
    );

}