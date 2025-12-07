package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.assignments.AssignmentCreateRequest;
import com.khangdjnh.edu_app.dto.request.assignments.AssignmentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.AssignmentFileResponse;
import com.khangdjnh.edu_app.dto.response.AssignmentResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AssignmentFileRepository;
import com.khangdjnh.edu_app.repository.AssignmentRepository;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final AssignmentFileService assignmentFileService;
    private final AssignmentFileRepository assignmentFileRepository;
    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;
    private final NotificationService notificationService;

    @Value("${file.upload-dir}")
    String uploadDir;

    @Transactional(rollbackFor = Exception.class)
    public AssignmentResponse createAssignment(AssignmentCreateRequest request) throws IOException {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .classEntity(classEntity)
                .build();

        assignment = assignmentRepository.save(assignment);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            assignmentFileService.saveFile(request.getFiles(), assignment);
        }

        assignment.setAssignmentFiles(assignmentFileRepository.findAllByAssignmentId(assignment.getId()));
        assignment = assignmentRepository.save(assignment);

        // Gửi thông báo
        List<User> students = classStudentRepository.findByClassEntity_Id(request.getClassId())
                .stream().map(ClassStudent::getStudent).toList();
        for (User student : students) {
            notificationService.sendNewAssignmentNotice(student, assignment.getTitle());
        }

        return mapAssignmentToResponse(assignment);
    }

    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        return mapAssignmentToResponse(assignment);
    }

    public List<AssignmentResponse> getAllAssignmentsByClassId(Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }

        return assignmentRepository.findAllByClassEntityIdOrderByCreatedAtDesc(classId).stream()
                .map(this::mapAssignmentToResponse)
                .collect(Collectors.toList());
    }

    public Page<AssignmentResponse> getAssignmentsForStudent(Long studentId, Long classId, String keyword,
                                                             LocalDateTime startFrom, LocalDateTime endTo,
                                                             int page, int size) {
        List<Long> classIds = classStudentRepository.findClassIdsByStudentId(studentId);
        if (classIds.isEmpty()) return Page.empty();

        if (classId != null) {
            classIds = classIds.stream().filter(id -> id.equals(classId)).toList();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("startAt").descending());
        Page<Assignment> assignments = assignmentRepository.searchAssignmentsForStudent(
                classIds, keyword, startFrom, endTo, pageable
        );

        return assignments.map(this::mapAssignmentToResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    public AssignmentResponse updateAssignment(AssignmentUpdateRequest request, Long id) throws IOException {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (request.getTitle() != null) assignment.setTitle(request.getTitle());
        if (request.getContent() != null) assignment.setContent(request.getContent());
        if (request.getStartAt() != null) assignment.setStartAt(request.getStartAt());
        if (request.getEndAt() != null) assignment.setEndAt(request.getEndAt());

        // Nếu có file mới upload
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            // Xóa hết file cũ
            assignment.getAssignmentFiles().clear();

            // Tạo danh sách file mới và add vào assignment
            for (MultipartFile file : request.getFiles()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                AssignmentFile assignmentFile = AssignmentFile.builder()
                        .assignment(assignment)
                        .fileName(file.getOriginalFilename())
                        .filePath(filePath.toString())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadedAt(LocalDateTime.now())
                        .build();

                assignment.getAssignmentFiles().add(assignmentFile);
            }
        }


        assignment = assignmentRepository.save(assignment);

        return mapAssignmentToResponse(assignment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAssignmentById(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        assignmentRepository.deleteById(id);
    }

    private AssignmentResponse mapAssignmentToResponse(Assignment assignment) {
        List<AssignmentFileResponse> fileResponses = Optional.ofNullable(assignment.getAssignmentFiles())
                .orElse(Collections.emptyList())
                .stream()
                .map(file -> AssignmentFileResponse.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .filePath(file.getFilePath())
                        .downloadUrl("/api/files/" + file.getId())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .classId(assignment.getClassEntity().getId())
                .files(fileResponses)
                .status(resolveAssignmentStatus(assignment.getStartAt(), assignment.getEndAt()))
                .startAt(assignment.getStartAt())
                .endAt(assignment.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .build();
    }

    private String resolveAssignmentStatus(LocalDateTime startAt, LocalDateTime endAt) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startAt)) return "Chưa bắt đầu";
        if (now.isAfter(endAt)) return "Đã hết hạn";
        return "Đang diễn ra";
    }
}
