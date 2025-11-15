package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.ExamInfo;
import com.khangdjnh.edu_app.dto.StudentScoreRow;
import com.khangdjnh.edu_app.dto.response.ScoreResponse;
import com.khangdjnh.edu_app.dto.response.ClassScoreSummaryResponse;
import com.khangdjnh.edu_app.entity.ClassStudent;
import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.Score;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import com.khangdjnh.edu_app.repository.ExamRepository;
import com.khangdjnh.edu_app.repository.ScoreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreService {
    ScoreRepository scoreRepository;
    ExamRepository examRepository;
    ClassStudentRepository classStudentRepository;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ScoreResponse> getAllScoreByClassIdExamId(Long classId, Long examId) {
        return scoreRepository.findAllByClassEntityIdAndExamId(classId, examId)
                .stream()
                .map(score -> ScoreResponse.builder()
                        .classId(score.getClassEntity().getId())
                        .studentId(score.getStudent().getId())
                        .ExamId(score.getExam().getId())
                        .score(score.getScore())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ClassScoreSummaryResponse getScoreSummaryByClassId(Long classId) {
        List<Exam> exams = examRepository.findAllByClassEntityIdOrderByCreatedAt(classId);
        List<User> students = classStudentRepository.findByClassEntity_Id(classId).stream().map(ClassStudent::getStudent).toList();
        List<Score> scores = scoreRepository.findAllByClassEntityId(classId); // Hoặc join theo student + exam + class

        // Convert exam list
        List<ExamInfo> examInfos = exams.stream()
                .map(e -> {
                    ExamInfo info = new ExamInfo();
                    info.setExamId(e.getId());
                    info.setName(e.getTitle());
                    return info;
                }).toList();

        // Map score theo studentId -> examId -> điểm
        Map<Long, Map<Long, BigDecimal>> scoreMap = scores.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStudent().getId(),
                        Collectors.toMap(
                                s -> s.getExam().getId(),
                                Score::getScore,
                                (existing, replacement) -> replacement
                        )
                ));

        // Tạo danh sách StudentScoreRow
        List<StudentScoreRow> rows = students.stream().map(s -> {
            Map<Long, BigDecimal> studentScores = scoreMap.getOrDefault(s.getId(), new HashMap<>());

            BigDecimal sum = studentScores.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal average = studentScores.isEmpty()
                    ? BigDecimal.ZERO
                    : sum.divide(BigDecimal.valueOf(studentScores.size()), 2, RoundingMode.HALF_UP);

            StudentScoreRow row = new StudentScoreRow();
            row.setStudentId(s.getId());
            row.setFullName(s.getFirstName() + " " + s.getLastName());
            row.setDob(s.getDob());
            row.setScores(studentScores);
            row.setAverageScore(average);

            return row;
        }).toList();

        ClassScoreSummaryResponse response = new ClassScoreSummaryResponse();
        response.setExams(examInfos);
        response.setStudents(rows);
        response.setClassId(classId);
        return response;
    }

}
