package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.question.SubmitAnswerRequest;
import com.khangdjnh.edu_app.entity.ExamAnswer;
import com.khangdjnh.edu_app.entity.ExamSubmission;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ExamAnswerRepository;
import com.khangdjnh.edu_app.repository.ExamSubmissionRepository;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamAnswerService {
    ExamAnswerRepository examAnswerRepository;
    ExamSubmissionRepository examSubmissionRepository;
    QuestionRepository questionRepository;

    @Transactional(rollbackFor = Exception.class)
    public void submitAnswer(Long submissionId, SubmitAnswerRequest request) {
        ExamSubmission submission = examSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        ExamAnswer answer = examAnswerRepository.findBySubmissionIdAndQuestionId(submissionId, request.getQuestionId())
                .orElse(ExamAnswer.builder().submission(submission).question(question).build());

        answer.setSelectedOption(request.getAnswerOption());
        examAnswerRepository.save(answer);
    }
}
