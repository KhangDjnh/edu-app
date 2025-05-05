package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.response.ExamQuestionResponse;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ExamQuestionService {

    private final QuestionRepository questionRepository;

    public Page<ExamQuestionResponse> getExamQuestions(Long examId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findQuestionsByExamId(examId, pageable);

        return questionPage.map(q -> ExamQuestionResponse.builder()
                .questionId(q.getId())
                .question(q.getQuestion())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .build()
        );
    }
}