package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.QuestionAnalysisDTO;
import com.khangdjnh.edu_app.dto.ScoreDistributionDTO;
import com.khangdjnh.edu_app.dto.StudentLearningDashboardDTO;
import com.khangdjnh.edu_app.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public List<QuestionAnalysisDTO> getQuestionAnalysisReport(Long examId) {
        return dashboardRepository.getAnalysisByExamId(examId);
    }

    public List<ScoreDistributionDTO> getScoreDistributionReport(Long examId) {
        return dashboardRepository.getScoreDistributionByExamId(examId);
    }

    public List<StudentLearningDashboardDTO> getLearningDashboardByClassId (Long classId) {
        return dashboardRepository.getLearningDashboardByClassId(classId);
    }

    public List<StudentLearningDashboardDTO> getLearningDashboardByStudentId (Long studentId) {
        return dashboardRepository.getLearningDashboardByStudentId(studentId);
    }

}
