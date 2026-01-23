package com.khangdjnh.edu_app.dto;

public record QuestionAnalysisDTO(
        Long examId,
        String examTitle,
        Long questionId,
        String questionContent,
        String correctAnswer,
        String difficultyLevel,
        Long totalResponses,
        Long totalCorrect,
        Long totalIncorrect,
        Double errorRate,
        String mostCommonWrongAnswer
) {}