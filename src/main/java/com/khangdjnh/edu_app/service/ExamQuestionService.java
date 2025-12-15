package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.response.ExamQuestionResponse;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExamQuestionService {

    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public Page<ExamQuestionResponse> getExamQuestions(Long examId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage =
                questionRepository.findQuestionsByExamId(examId, pageable);

        // 1. Lấy content
        List<Question> questions = new ArrayList<>(questionPage.getContent());

        // 2. Xáo trộn thứ tự
        Collections.shuffle(questions);

        // 3. Map sang response
        List<ExamQuestionResponse> responses = questions.stream()
                .map(q -> ExamQuestionResponse.builder()
                        .questionId(q.getId())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .build()
                )
                .toList();

        // 4. Tạo Page mới
        return new PageImpl<>(
                responses,
                pageable,
                questionPage.getTotalElements()
        );
    }

}