package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.question.QuestionCreateRequest;
import com.khangdjnh.edu_app.dto.request.question.QuestionSearchRequest;
import com.khangdjnh.edu_app.dto.response.QuestionDetailResponse;
import com.khangdjnh.edu_app.dto.response.QuestionResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionRepository examQuestionRepository;
    private final ClassRepository classRepository;

//    public Page<QuestionResponse> searchQuestions(QuestionSearchRequest request, Pageable pageable) {
//        Specification<Question> spec = (root, query, cb) -> {
//            Predicate predicate = cb.conjunction();
//
//            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
//                predicate = cb.and(predicate, cb.like(cb.lower(root.get("question")), "%" + request.getKeyword().toLowerCase() + "%"));
//            }
//
//            if (request.getClassId() != null) {
//                predicate = cb.and(predicate, cb.equal(root.get("classEntity").get("id"), request.getClassId()));
//            }
//
//            if (request.getChapter() != null) {
//                predicate = cb.and(predicate, cb.equal(root.get("chapter"), request.getChapter()));
//            }
//
//            if (request.getLevel() != null) {
//                predicate = cb.and(predicate, cb.equal(root.get("level"), request.getLevel()));
//            }
//
//            return predicate;
//        };
//
//        return examQuestionRepository.findAll(spec, pageable)
//                .map(q -> QuestionResponse.builder()
//                        .id(q.getId())
//                        .classId(q.getClassEntity().getId())
//                        .chapter(q.getChapter())
//                        .question(q.getQuestion())
//                        .answer(q.getAnswer())
//                        .level(q.getLevel())
//                        .build());
//    }

    public Page<QuestionResponse> searchQuestions(QuestionSearchRequest request, Pageable pageable) {
        Specification<Question> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Thêm điều kiện keyword
            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("question")),
                        "%" + request.getKeyword().toLowerCase() + "%"));
            }

            // Thêm điều kiện classId
            if (request.getClassId() != null) {
                predicates.add(cb.equal(root.get("classEntity").get("id"), request.getClassId()));
            }

            // Thêm điều kiện chapter
            if (request.getChapter() != null) {
                predicates.add(cb.equal(root.get("chapter"), request.getChapter()));
            }

            // Thêm điều kiện level
            if (request.getLevel() != null) {
                predicates.add(cb.equal(root.get("level"), request.getLevel()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return examQuestionRepository.findAll(spec, pageable)
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .classId(q.getClassEntity().getId())
                        .chapter(q.getChapter())
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .level(q.getLevel())
                        .build());
    }

    public QuestionResponse createQuestion (QuestionCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Question question = Question.builder()
                .classEntity(classEntity)
                .question(request.getQuestion())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .answer(request.getAnswer())
                .level(request.getLevel())
                .chapter(request.getChapter())
                .build();

        question = examQuestionRepository.save(question);

        return QuestionResponse.builder()
                .id(question.getId())
                .classId(classEntity.getId())
                .chapter(question.getChapter())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public QuestionResponse updateQuestion(Long id, QuestionCreateRequest request) {
        Question question = examQuestionRepository.findById(id)
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
        question.setChapter(request.getChapter());

        question = examQuestionRepository.save(question);

        return QuestionResponse.builder()
                .id(question.getId())
                .chapter(question.getChapter())
                .question(question.getQuestion())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public QuestionDetailResponse getQuestionById(Long id) {
        Question question = examQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam question not found"));

        return QuestionDetailResponse.builder()
                .id(question.getId())
                .classId(question.getClassEntity().getId())
                .chapter(question.getChapter())
                .question(question.getQuestion())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .answer(question.getAnswer())
                .level(question.getLevel())
                .build();
    }

    public Page<QuestionResponse> getAllQuestionByClass(Long classId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Question> questions = examQuestionRepository.findByClassEntityId(classId, pageable);

        return questions.map(q -> QuestionResponse.builder()
                .id(q.getId())
                .classId(q.getClassEntity().getId())
                .chapter(q.getChapter())
                .question(q.getQuestion())
                .answer(q.getAnswer())
                .level(q.getLevel())
                .build());
    }

    public void deleteQuestion(Long id) {
        if (!examQuestionRepository.existsById(id)) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
        }
        examQuestionRepository.deleteById(id);
    }


}
