package com.khangdjnh.edu_app.dto;

import java.time.LocalDateTime;

public record ScoreDistributionDTO(
        Long examId,
        String examTitle,
        String className,
        Double score,
        String scoreRange,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Long timeSpentMinutes
) {}