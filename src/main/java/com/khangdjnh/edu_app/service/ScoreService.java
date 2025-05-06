package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.response.ScoreResponse;
import com.khangdjnh.edu_app.entity.Score;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ScoreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreService {
    ScoreRepository scoreRepository;

    public List<ScoreResponse> getAllScoreByStudentId(Long studentId) {
        return scoreRepository.findAllByStudentId(studentId)
                .stream()
                .map(score -> ScoreResponse.builder()
                        .classId(score.getClassEntity().getId())
                        .studentId(score.getStudent().getId())
                        .ExamId(score.getExam().getId())
                        .score(score.getScore())
                        .build())
                .toList();
    }

    public List<ScoreResponse> getAllScoreByExamId(Long examId) {
        return scoreRepository.findAllByExamId(examId)
                .stream()
                .map(score -> ScoreResponse.builder()
                        .classId(score.getClassEntity().getId())
                        .studentId(score.getStudent().getId())
                        .ExamId(score.getExam().getId())
                        .score(score.getScore())
                        .build())
                .toList();
    }

    public ScoreResponse getScoreByExamIdAndStudentId(Long studentId, Long examId) {
        Score score = scoreRepository.findByStudentIdAndExamId(studentId, examId)
                .orElseThrow(() -> new AppException(ErrorCode.SCORES_NOT_FOUND));
        return ScoreResponse.builder()
                .classId(score.getClassEntity().getId())
                .studentId(score.getStudent().getId())
                .ExamId(score.getExam().getId())
                .score(score.getScore())
                .build();
    }
}
