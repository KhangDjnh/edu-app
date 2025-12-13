package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.question.QuestionCreateRequest;
import com.khangdjnh.edu_app.dto.request.question.QuestionSearchRequest;
import com.khangdjnh.edu_app.dto.response.QuestionDetailResponse;
import com.khangdjnh.edu_app.dto.response.QuestionResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Question;
import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionService {
    QuestionRepository examQuestionRepository;
    private final ClassRepository classRepository;
    private final QuestionRepository questionRepository;

    public Page<QuestionDetailResponse> searchQuestions(QuestionSearchRequest request, Pageable pageable) {
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
                .map(q -> QuestionDetailResponse.builder()
                        .id(q.getId())
                        .classId(q.getClassEntity().getId())
                        .chapter(q.getChapter())
                        .question(q.getQuestion())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .answer(q.getAnswer())
                        .level(q.getLevel())
                        .build());
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Page<QuestionDetailResponse> getAllQuestionByClass(Long classId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Question> questions = examQuestionRepository.findByClassEntityId(classId, pageable);

        return questions.map(q -> QuestionDetailResponse.builder()
                .id(q.getId())
                .classId(q.getClassEntity().getId())
                .chapter(q.getChapter())
                .question(q.getQuestion())
                .optionA(q.getOptionA())
                .optionB(q.getOptionB())
                .optionC(q.getOptionC())
                .optionD(q.getOptionD())
                .answer(q.getAnswer())
                .level(q.getLevel())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long id) {
        if (!examQuestionRepository.existsById(id)) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
        }
        examQuestionRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void importQuestionsFromExcel(Long classId, MultipartFile file) {
        List<Question> questions = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Bỏ qua dòng header (bắt đầu từ row 1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || getCellString(row, 0).isEmpty()) continue;

                String questionText = getCellString(row, 1);
                String optionA = getCellString(row, 2);
                String optionB = getCellString(row, 3);
                String optionC = getCellString(row, 4);
                String optionD = getCellString(row, 5);
                String answer = getCellString(row, 6);
                Integer chapter = Integer.valueOf(getCellString(row, 7));
                String level = getCellString(row, 8);

                Question q = Question.builder()
                        .classEntity(ClassEntity.builder().id(classId).build())
                        .chapter(chapter)
                        .question(questionText)
                        .optionA(optionA)
                        .optionB(optionB)
                        .optionC(optionC)
                        .optionD(optionD)
                        .answer(AnswerOption.valueOf(answer.toUpperCase()))
                        .level(QuestionLevel.valueOf(level.toUpperCase()))
                        .build();

                questions.add(q);
            }
            questionRepository.saveAll(questions);
        } catch (Exception e) {
            throw new RuntimeException("Error while importing Excel: " + e.getMessage());
        }

    }


    // Helper đọc cell an toàn
    private String getCellString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

}
