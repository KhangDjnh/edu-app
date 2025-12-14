package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.response.ExamSubmissionResultResponse;
import com.khangdjnh.edu_app.dto.response.StartExamResponse;
import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.ExamAnswer;
import com.khangdjnh.edu_app.entity.ExamSubmission;
import com.khangdjnh.edu_app.entity.Score;
import com.khangdjnh.edu_app.enums.ExamSubmissionStatus;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExamSubmissionService {

    ExamRepository examRepository;
    ExamSubmissionRepository examSubmissionRepository;
    UserRepository userRepository;
    ExamAnswerRepository examAnswerRepository;
    ScoreRepository scoreRepository;

    @Transactional(rollbackFor = Exception.class)
    public StartExamResponse startExam(Long examId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var currentStudent = userRepository.findByKeycloakUserId(userKeycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // Kiểm tra thời gian hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new AppException(ErrorCode.EXAM_NOT_AVAILABLE);
        }

        ExamSubmission submission = examSubmissionRepository.findByExamIdAndStudentId(examId, currentStudent.getId())
                .orElseGet(() -> {
                    // Tạo mới nếu chưa tồn tại
                    ExamSubmission newSubmission = ExamSubmission.builder()
                            .exam(exam)
                            .student(currentStudent)
                            .status(ExamSubmissionStatus.IN_PROGRESS)
                            .startedAt(now)
                            .build();
                    return examSubmissionRepository.save(newSubmission);
                });
        List<ExamAnswer> listExamAnswers = examAnswerRepository.findAllBySubmissionId(submission.getId());

        return StartExamResponse.builder()
                .submissionId(submission.getId())
                .examTitle(exam.getTitle())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .status(submission.getStatus())
                .startedAt(submission.getStartedAt())
                .completedAt(submission.getCompletedAt())
                .score(submission.getScore())
                .listExamAnswers(listExamAnswers == null ? new ArrayList<>() : listExamAnswers)
                .build();
    }

    @Transactional(readOnly = true)
    public ExamSubmissionResultResponse getExamSubmission(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKeycloakId = authentication.getName();
        var currentStudent = userRepository.findByKeycloakUserId(userKeycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        ExamSubmission result =  examSubmissionRepository.findByExamIdAndStudentId(exam.getId(), currentStudent.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        return ExamSubmissionResultResponse.builder()
                .id(result.getId())
                .studentId(currentStudent.getId())
                .examId(exam.getId())
                .studentName(currentStudent.getFirstName() + " " + currentStudent.getLastName())
                .status(result.getStatus())
                .build();
    }


    @Transactional(rollbackFor = Exception.class)
    public void submitExam(Long submissionId) {
        ExamSubmission submission = examSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        if (submission.getCompletedAt() != null)
            throw new AppException(ErrorCode.ALREADY_SUBMITTED);

        List<ExamAnswer> answers = examAnswerRepository.findAllBySubmissionId(submissionId);
        if (answers.isEmpty()) {
            log.warn("No answers found for submissionId={}", submissionId);
            throw new AppException(ErrorCode.NO_ANSWER_FOUND_IN_SUBMISSION);
        }
        BigDecimal scoreValue = calculateScore(answers);
        submission.setScore(scoreValue);
        submission.setCompletedAt(LocalDateTime.now());
        submission.setStatus(ExamSubmissionStatus.COMPLETED);
        examSubmissionRepository.save(submission);

        Optional<Score> existingScore = scoreRepository.findByStudentIdAndExamId(
                submission.getStudent().getId(), submission.getExam().getId()
        );
        if (existingScore.isEmpty()) {
            Score score = Score.builder()
                    .classEntity(submission.getExam().getClassEntity())
                    .student(submission.getStudent())
                    .exam(submission.getExam())
                    .score(scoreValue)
                    .build();
            scoreRepository.save(score);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getScoreBySubmissionId (Long submissionId) {
        ExamSubmission result = examSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        return result.getScore();
    }

    private BigDecimal calculateScore(List<ExamAnswer> answers) {
        long correctCount = answers.stream()
                .filter(ans -> ans.getSelectedOption() == ans.getQuestion().getAnswer())
                .count();

        return BigDecimal.valueOf(correctCount * 10.0 / answers.size())
                .setScale(2, RoundingMode.HALF_UP);
    }

}