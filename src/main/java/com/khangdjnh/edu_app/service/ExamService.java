package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.exam.ExamCreateChooseRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamCreateRandomRequest;
import com.khangdjnh.edu_app.dto.request.exam.ExamUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ExamResponse;
import com.khangdjnh.edu_app.dto.response.MarkExamStartResponse;
import com.khangdjnh.edu_app.dto.response.StudentExamResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.enums.*;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.RoomMapper;
import com.khangdjnh.edu_app.repository.*;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final ClassRepository classRepository;
    private final QuestionRepository examQuestionRepository;
    private final ClassStudentRepository classStudentRepository;
    private final NotificationService notificationService;
    private final ExamSubmissionRepository examSubmissionRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final AttendanceRepository attendanceRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

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
        List<?> tempList = new ArrayList<>(allQuestions);
        Collections.shuffle(tempList);

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
    public List<StudentExamResponse> getExamsInClassIdByStudent(Long classId) {
        String studentEmail = SecurityUtils.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return examRepository.findAllByClassEntityId(classId)
                .stream()
                .map(item -> toStudentExamResponse(item, currentUser.getId()))
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
    public MarkExamStartResponse markExamStarted (Long examId) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if(exam.getIsStarted()) {
            throw new AppException(ErrorCode.EXAM_ALREADY_STARTED);
        }
        exam.setIsStarted(true);
        examRepository.save(exam);

        String roomCode = "EXAM-" + exam.getId();

        Room room = Room.builder()
                .roomName(exam.getTitle())
                .roomCode(roomCode)
                .teacherId(exam.getClassEntity().getTeacher().getId())
                .classId(exam.getClassEntity().getId())
                .startTime(LocalDateTime.now())
                .status(RoomStatus.STARTED)
                .isActive(true)
                .createdBy(SecurityUtils.getCurrentUsername())
                .exam(exam)
                .build();
        room = roomRepository.save(room);

        String classRoomPath = frontendUrl + "/classRoom?roomCode=" + roomCode + "&roomId=" + room.getId() + "&userId="
                + currentUser.getId() + "&userName=" + currentUser.getUsername();
        room.setClassRoomPath(classRoomPath);
        roomRepository.save(room);

        // get data students in class
        ClassEntity classEntity = exam.getClassEntity();
        List<ClassStudent> listStudents = classStudentRepository.findByClassEntity_Id(classEntity.getId());
        List<User> students = listStudents.stream().map(ClassStudent::getStudent).toList();

        //create attendance in class
        List<Attendance> listAttendances = new ArrayList<>();
        for (User student : students) {
            Attendance attendance = Attendance.builder()
                    .student(student)
                    .classEntity(classEntity)
                    .attendanceDate(LocalDate.now())
                    .status(AttendanceStatus.ABSENT)
                    .exam(exam)
                    .build();
            listAttendances.add(attendance);
        }
        attendanceRepository.saveAll(listAttendances);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Format lại thời gian bắt đầu kỳ thi
        String examStartTime = exam.getStartTime().format(formatter);
        String content = "Giáo viên của bạn lớp " + exam.getClassEntity().getName()
                + " đã bắt đầu kì thi " + exam.getTitle()
                + " lúc " + examStartTime
                + ". Bạn nhớ tham gia kì thi đúng giờ nhé!";

        for (User student : students) {
            notificationService.sendNewExamStartNotice(student, content, exam);
        }

        return MarkExamStartResponse.builder()
                .id(exam.getId())
                .classId(exam.getClassEntity().getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .createdAt(exam.getCreatedAt())
                .room(roomMapper.toRoomResponse(room))
                .build();
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

    private StudentExamResponse toStudentExamResponse(Exam exam, Long studentId) {
        ExamSubmission submission = examSubmissionRepository.findByExamIdAndStudentId(exam.getId(), studentId)
                .orElse(null);
        StudentExamResponse result = StudentExamResponse.builder()
                .id(exam.getId())
                .classId(exam.getClassEntity().getId())
                .title(exam.getTitle())
                .startTime(exam.getStartTime())
                .endTime(exam.getEndTime())
                .description(exam.getDescription())
                .createdAt(exam.getCreatedAt())
                .isStarted(exam.getIsStarted())
                .build();
        result.setStatus(submission == null ? ExamSubmissionStatus.NOT_STARTED : submission.getStatus());
        return result;
    }
}
