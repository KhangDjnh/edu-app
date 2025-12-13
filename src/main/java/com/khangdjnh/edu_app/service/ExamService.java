package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.exam.ExamCreateChooseRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamCreateRandomRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ExamResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import com.khangdjnh.edu_app.repository.QuestionRepository;
import com.khangdjnh.edu_app.repository.ExamRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamService {
    ExamRepository examRepository;
    ClassRepository classRepository;
    QuestionRepository examQuestionRepository;
    ClassStudentRepository classStudentRepository;
    NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public ExamResponse createRandomExam(ExamCreateRandomRequest request) {
        ClassEntity classEntity = validateClass(request.getClassId());
        validateExamTime(request.getStartTime(), request.getEndTime());

        List<Question> classQuestions = examQuestionRepository.findByClassEntityId(request.getClassId());

        Set<Question> allQuestions = new HashSet<>();
        allQuestions.addAll(getRandomQuestionsFromList(classQuestions, QuestionLevel.EASY, request.getNumberOfEasyQuestions()));
        allQuestions.addAll(getRandomQuestionsFromList(classQuestions, QuestionLevel.MEDIUM, request.getNumberOfMediumQuestions()));
        allQuestions.addAll(getRandomQuestionsFromList(classQuestions, QuestionLevel.HARD, request.getNumberOfHardQuestions()));
        allQuestions.addAll(getRandomQuestionsFromList(classQuestions, QuestionLevel.VERY_HARD, request.getNumberOfVeryHardQuestions()));
        Collections.shuffle((List<?>) allQuestions);

        Exam exam = Exam.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .description(request.getDescription())
                .isStarted(false)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(allQuestions)
                .build();

        return toExamResponse(examRepository.save(exam));
    }

    @Transactional(rollbackFor = Exception.class)
    public ExamResponse createChooseExam(ExamCreateChooseRequest request) {
        ClassEntity classEntity = validateClass(request.getClassId());
        validateExamTime(request.getStartTime(), request.getEndTime());

        List<Question> questions = getValidatedQuestions(request.getQuestionIds(), request.getClassId());

        Set<Question> questionSet = new HashSet<>(questions);

        Exam exam = Exam.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .description(request.getDescription())
                .isStarted(false)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .questions(questionSet)
                .build();

        return toExamResponse(examRepository.save(exam));
    }

    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return toExamResponse(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByClassId(Long classId) {
        return examRepository.findAllByClassEntityIdOrderByCreatedAtDesc(classId)
                .stream()
                .map(this::toExamResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsInClassIdByStudent(Long classId) {
        return examRepository.findAllByClassEntityIdAndIsStarted(classId, true)
                .stream()
                .map(this::toExamResponse)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public ExamResponse updateExam(Long examId, ExamUpdateRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        ClassEntity classEntity = validateClass(request.getClassId());
        validateExamTime(request.getStartTime(), request.getEndTime());

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setClassEntity(classEntity);

        return toExamResponse(examRepository.save(exam));
    }

    @Transactional(rollbackFor = Exception.class)
    public ExamResponse markExamStarted (Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        exam.setIsStarted(true);
        examRepository.save(exam);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Format lại thời gian bắt đầu kỳ thi
        String examStartTime = exam.getStartTime().format(formatter);
        List<User> students = classStudentRepository.findByClassEntity_Id(exam.getClassEntity().getId())
                .stream().map(ClassStudent::getStudent).toList();
        String content = "Giáo viên của bạn lớp " + exam.getClassEntity().getName()
                + " đã bắt đầu kì thi " + exam.getTitle()
                + " lúc " + examStartTime
                + ". Bạn nhớ tham gia kì thi đúng giờ nhé!";

        for (User student : students) {
            notificationService.sendNewExamStartNotice(student, content, exam);
        }

        return toExamResponse(exam);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // Xóa liên kết với question trước (clear bảng nối)
        exam.getQuestions().clear();
        examRepository.save(exam);

        // Sau đó mới xóa exam
        examRepository.delete(exam);
    }

    private ClassEntity validateClass(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
    }

    private void validateExamTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new AppException(ErrorCode.INVALID_EXAM_TIME);
        }
    }

    private List<Question> getValidatedQuestions(List<Long> questionIds, Long classId) {
        if (questionIds == null || questionIds.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_QUESTION_LIST);
        }

        List<Question> questions = examQuestionRepository.findAllById(questionIds);

        if (questions.size() != questionIds.size()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
        }

        for (Question q : questions) {
            if (!q.getClassEntity().getId().equals(classId)) {
                throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
            }
        }

        return questions;
    }

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

    private ExamResponse toExamResponse(Exam exam) {
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
}
