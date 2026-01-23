package com.khangdjnh.edu_app.repository;

import com.khangdjnh.edu_app.dto.QuestionAnalysisDTO;
import com.khangdjnh.edu_app.dto.ScoreDistributionDTO;
import com.khangdjnh.edu_app.dto.StudentLearningDashboardDTO;

import java.util.List;

public interface DashboardRepository {
    List<QuestionAnalysisDTO> getAnalysisByExamId(Long examId);

    List<ScoreDistributionDTO> getScoreDistributionByExamId(Long examId);

    List<StudentLearningDashboardDTO> getLearningDashboardByClassId(Long classId);

    List<StudentLearningDashboardDTO> getLearningDashboardByStudentId(Long studentId);
}
