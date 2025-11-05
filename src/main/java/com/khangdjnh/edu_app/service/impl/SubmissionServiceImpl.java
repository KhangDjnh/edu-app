package com.khangdjnh.edu_app.service.impl;

import com.khangdjnh.edu_app.dto.request.submission.GradeRequest;
import com.khangdjnh.edu_app.dto.request.submission.SubmissionRequest;
import com.khangdjnh.edu_app.dto.response.SubmissionFileResponse;
import com.khangdjnh.edu_app.dto.response.SubmissionResponse;
import com.khangdjnh.edu_app.entity.Assignment;
import com.khangdjnh.edu_app.entity.Submission;
import com.khangdjnh.edu_app.entity.SubmissionFile;
import com.khangdjnh.edu_app.entity.User;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AssignmentRepository;
import com.khangdjnh.edu_app.repository.SubmissionFileRepository;
import com.khangdjnh.edu_app.repository.SubmissionRepository;
import com.khangdjnh.edu_app.repository.UserRepository;
import com.khangdjnh.edu_app.service.NotificationService;
import com.khangdjnh.edu_app.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmissionResponse createSubmission(SubmissionRequest request) {
        User student = userRepository.findById(request.getStudentId()).orElseThrow();
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElseThrow();

        Submission submission = Submission.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .submittedAt(LocalDateTime.now())
                .assignment(assignment)
                .student(student)
                .build();
        submission = submissionRepository.save(submission);

        Submission finalSubmission = submission;
        List<SubmissionFile> files = request.getFiles().stream().map(file -> {
            String filePath = "uploads/" + file.getOriginalFilename();
            SubmissionFile sf = SubmissionFile.builder()
                    .submission(finalSubmission)
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .uploadedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
            return submissionFileRepository.save(sf);
        }).collect(Collectors.toList());

        submission.setSubmissionFiles(files);

        User teacher = assignment.getClassEntity().getTeacher();
        String content = "Sinh viên " + student.getFirstName() + " " + student.getLastName() + " đã nộp bài cho bài tập \"" + assignment.getTitle() + "\".";
        notificationService.sendNewAssignmentNotice(teacher, content);

        return toDto(submission);
    }

    @Transactional(readOnly = true)
    @Override
    public SubmissionResponse getSubmissionById(Long id) {
        return toDto(submissionRepository.findWithFilesById(id).orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findWithFilesByAssignmentId(assignmentId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public SubmissionResponse getSubmissionsByAssignmentAndStudent(Long assignmentId, Long studentId) {
        Submission submission = submissionRepository
                .findWithFilesByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        return toDto(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudentIdAndClassId(Long studentId, Long classId) {
        List<Submission> list1 = submissionRepository.findWithFilesByStudentIdAndClassId(studentId, classId);
        return list1.stream().map(this::toDto).toList();
    }


    @Override
    public SubmissionResponse gradeSubmission(Long id, GradeRequest request) {
        Submission submission = submissionRepository.findById(id).orElseThrow();
        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());

        Submission updated = submissionRepository.save(submission);

        // Gửi thông báo cho sinh viên
        User student = updated.getStudent();
        String content = "Bài nộp \"" + updated.getTitle() + "\" đã được chấm: " +
                "Điểm: " + request.getGrade() + ", Nhận xét: \"" + request.getFeedback() + "\".";
        notificationService.sendLeaveNotice(student, content);

        return toDto(updated);
    }


    @Override
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    private SubmissionResponse toDto(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .title(submission.getTitle())
                .content(submission.getContent())
                .submittedAt(submission.getSubmittedAt())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .assignmentId(submission.getAssignment().getId())
                .studentId(submission.getStudent().getId())
                .files(submission.getSubmissionFiles().stream().map(file -> SubmissionFileResponse.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .filePath(file.getFilePath())
                        .fileType(file.getFileType())
                        .downloadUrl("/api/files/" + file.getId())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}