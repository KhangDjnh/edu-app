package com.khangdjnh.edu_app.dto;

public record StudentLearningDashboardDTO(
        Long studentId,
        String studentName,
        String studentEmail,
        String studentAvatar,
        Long classId,
        String className,
        String semester,
        Integer presentNumber,
        Integer absenceNumber,
        Integer lateNumber,
        Double attendanceRate,
        Double avgExamScore,
        Long examsCompleted,
        Double avgAssignmentGrade,
        Long assignmentsSubmitted,
        Long totalAssignmentsInClass,
        String assignmentCompletionRatio,
        Double overallScore
) {}