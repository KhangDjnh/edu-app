package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.examquestion.ExamQuestionCreateRequest;
import com.khangdjnh.edu_app.dto.response.ExamQuestionResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.ExamQuestion;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.ExamQuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamQuestionService {
    ExamQuestionRepository examQuestionRepository;
    private final ClassRepository classRepository;

    public ExamQuestionResponse createQuestion (ExamQuestionCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        ExamQuestion question = ExamQuestion.builder()
                .classEntity(classEntity)
                .question(request.getQuestion())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .answer(request.getAnswer())
                .level(request.getLevel())
                .build();

        question = examQuestionRepository.save(question);

        return ExamQuestionResponse.builder()
                .id(question.getId())
                .classId(classEntity.getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public ExamQuestionResponse updateQuestion(Long id, ExamQuestionCreateRequest request) {
        ExamQuestion question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam question not found"));
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        question.setClassEntity(classEntity);
        question.setQuestion(request.getQuestion());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setAnswer(request.getAnswer());
        question.setLevel(request.getLevel());

        question = examQuestionRepository.save(question);

        return ExamQuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public ExamQuestionResponse getQuestionById(Long id) {
        ExamQuestion question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam question not found"));

        return ExamQuestionResponse.builder()
                .id(question.getId())
                .classId(question.getClassEntity().getId())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public List<ExamQuestionResponse> getAllQuestionByClass(Long classId) {
        return examQuestionRepository.findByClassEntityId(classId)
                .stream()
                .map(q -> ExamQuestionResponse.builder()
                        .id(q.getId())
                        .classId(q.getClassEntity().getId())
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .level(q.getLevel())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteQuestion(Long id) {
        if (!examQuestionRepository.existsById(id)) {
            throw new RuntimeException("Exam question not found");
        }
        examQuestionRepository.deleteById(id);
    }


}
