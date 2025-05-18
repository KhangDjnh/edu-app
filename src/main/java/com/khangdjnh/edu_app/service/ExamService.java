package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.exam.ExamCreateChooseRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamCreateRandomRequest;
import com.khangdjnh.edu_app.dto.response.ExamResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import com.khangdjnh.edu_app.repository.ExamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {
    ExamRepository examRepository;
    ClassRepository classRepository;
    QuestionRepository examQuestionRepository;

    private List<Question> getRandomQuestionsFromList(List<Question> questions, QuestionLevel level, int count) {
        List<Question> filtered = questions.stream()
                .filter(q -> q.getLevel() == level)
                .collect(Collectors.toList());

        if (filtered.size() < count) {
            throw new AppException(ErrorCode.NOT_ENOUGH_QUESTIONS);
        }

        Collections.shuffle(filtered);
        return filtered.subList(0, count);
    }


    @Transactional
    public ExamResponse createRandomExam(ExamCreateRandomRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_EXAM_TIME);
        }

        // 1. Lấy tất cả câu hỏi thuộc classId
        List<Question> classQuestions = examQuestionRepository.findByClassEntityId(request.getClassId());

        // 2. Lọc câu hỏi theo độ khó từ danh sách ở bước 1
        List<Question> easy = getRandomQuestionsFromList(classQuestions, QuestionLevel.EASY, request.getNumberOfEasyQuestions());
        List<Question> medium = getRandomQuestionsFromList(classQuestions, QuestionLevel.MEDIUM, request.getNumberOfMediumQuestions());
        List<Question> hard = getRandomQuestionsFromList(classQuestions, QuestionLevel.HARD, request.getNumberOfHardQuestions());
        List<Question> veryHard = getRandomQuestionsFromList(classQuestions, QuestionLevel.VERY_HARD, request.getNumberOfVeryHardQuestions());

        // 3. Tổng hợp và ngẫu nhiên hóa thứ tự
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(easy);
        allQuestions.addAll(medium);
        allQuestions.addAll(hard);
        allQuestions.addAll(veryHard);
        Collections.shuffle(allQuestions);

        // 4. Tạo và lưu exam
        Exam exam = Exam.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(allQuestions)
                .build();

        exam = examRepository.save(exam);

        return ExamResponse.builder()
                .id(exam.getId())
                .classId(classEntity.getId())
                .title(exam.getTitle())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .description(exam.getDescription())
                .createdAt(exam.getCreatedAt())
                .build();
    }

    public ExamResponse createChooseExam(ExamCreateChooseRequest request) {
        // Validate class
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_EXAM_TIME);
        }

        if (request.getQuestionIds() == null || request.getQuestionIds().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_QUESTION_LIST);
        }

        List<Question> questions = examQuestionRepository.findAllById(request.getQuestionIds());

        if (questions.size() != request.getQuestionIds().size()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
        }

        for (Question q : questions) {
            if (!q.getClassEntity().getId().equals(classEntity.getId())) {
                throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
            }
        }

        Exam exam = Exam.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(questions)
                .build();

        exam = examRepository.save(exam);

        return ExamResponse.builder()
                .id(exam.getId())
                .classId(classEntity.getId())
                .title(exam.getTitle())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .description(exam.getDescription())
                .createdAt(exam.getCreatedAt())
                .build();
    }


    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return ExamResponse.builder()
                .id(exam.getId())
                .classId(exam.getClassEntity().getId())
                .title(exam.getTitle())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .description(exam.getDescription())
                .createdAt(exam.getCreatedAt())
                .build();
    }
    public List<ExamResponse> getExamsByClassId (Long classId) {
        return examRepository.findAllByClassEntityId(classId)
                .stream()
                .map( exam -> ExamResponse.builder()
                        .id(exam.getId())
                        .classId(exam.getClassEntity().getId())
                        .title(exam.getTitle())
                        .startTime(exam.getStartTime())
                        .endTime(exam.getEndTime())
                        .description(exam.getDescription())
                        .createdAt(exam.getCreatedAt())
                        .build()
                )
                .toList();
    }

}
