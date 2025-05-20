package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.assignments.AssignmentCreateRequest;
import com.khangdjnh.edu_app.dto.request.assignments.AssignmentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.AssignmentFileResponse;
import com.khangdjnh.edu_app.dto.response.AssignmentResponse;
import com.khangdjnh.edu_app.entity.*;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AssignmentRepository;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.ClassStudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentService {
    final AssignmentRepository assignmentRepository;
    final AssignmentFileService assignmentFileService;
    final ClassRepository classRepository;
    final ClassStudentRepository classStudentRepository;
    final NotificationService notificationService;

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
        List<AssignmentFileResponse> fileResponses = assignment.getAssignmentFiles()
                .stream()
                .map(file -> AssignmentFileResponse.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .filePath(file.getFilePath())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .build())
                .toList();
        String status;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(assignment.getStartAt())) {
            status = "Chưa bắt đầu";
        } else if (now.isAfter(assignment.getEndAt())) {
            status = "Đã hết hạn";
        } else {
            status = "Đang diễn ra";
        }

        List<User> students = classStudentRepository.findByClassEntity_Id(request.getClassId()).stream().map(ClassStudent::getStudent).toList();
        for(User student : students) {
            notificationService.sendNewAssignmentNotice(student, assignment.getTitle());
        }

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .classId(request.getClassId())
                .status(status)
                .files(fileResponses)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .build();
    }

    public AssignmentResponse getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        List<AssignmentFileResponse> fileResponses = assignment.getAssignmentFiles()
                .stream()
                .map(file -> AssignmentFileResponse.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .filePath(file.getFilePath())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .build())
                .toList();
        String status;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(assignment.getStartAt())) {
            status = "Chưa bắt đầu";
        } else if (now.isAfter(assignment.getEndAt())) {
            status = "Đã hết hạn";
        } else {
            status = "Đang diễn ra";
        }

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .classId(assignment.getClassEntity().getId())
                .status(status)
                .startAt(assignment.getStartAt())
                .endAt(assignment.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .files(fileResponses)
                .build();
    }

    public List<AssignmentResponse> getAllAssignmentsByClassId(Long classId){
        if(!classRepository.existsById(classId)){
            throw new AppException(ErrorCode.CLASS_NOT_FOUND);
        }
        List<Assignment> assignments = assignmentRepository.findAllByClassIdOrderByCreatedAtDesc(classId);
        List<AssignmentResponse> responses = new ArrayList<>();
        for(Assignment assignment : assignments) {
            List<AssignmentFileResponse> fileResponses = assignment.getAssignmentFiles()
                    .stream()
                    .map(file -> AssignmentFileResponse.builder()
                            .fileName(file.getFileName())
                            .fileType(file.getFileType())
                            .filePath(file.getFilePath())
                            .fileSize(file.getFileSize())
                            .uploadedAt(file.getUploadedAt())
                            .build())
                    .toList();
            String status;
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(assignment.getStartAt())) {
                status = "Chưa bắt đầu";
            } else if (now.isAfter(assignment.getEndAt())) {
                status = "Đã hết hạn";
            } else {
                status = "Đang diễn ra";
            }

            AssignmentResponse assignmentResponse = AssignmentResponse.builder()
                    .id(assignment.getId())
                    .title(assignment.getTitle())
                    .content(assignment.getContent())
                    .classId(assignment.getClassEntity().getId())
                    .status(status)
                    .startAt(assignment.getStartAt())
                    .endAt(assignment.getEndAt())
                    .createdAt(assignment.getCreatedAt())
                    .files(fileResponses)
                    .build();
            responses.add(assignmentResponse);
        }
        return responses;
    }

    public Page<AssignmentResponse> getAssignmentsForStudent(Long studentId, Long classId, String keyword,
                                                             LocalDateTime startFrom, LocalDateTime endTo,
                                                             int page, int size) {
        // Lấy danh sách classId mà sinh viên đang tham gia
        List<Long> classIds = classStudentRepository.findClassIdsByStudentId(studentId); // join bảng student_class

        if (classIds.isEmpty()) return Page.empty();

        // Nếu truyền classId thì chỉ lọc theo classId đó
        if (classId != null) {
            classIds = classIds.stream().filter(id -> id.equals(classId)).toList();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("startAt").descending());

        Page<Assignment> assignments = assignmentRepository.searchAssignmentsForStudent(
                classIds, keyword, startFrom, endTo, pageable
        );

        return assignments.map(this::mapToResponse);
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        List<AssignmentFile> assignmentFiles = assignment.getAssignmentFiles();
        List<AssignmentFileResponse> fileResponses = new ArrayList<>();
        if (assignmentFiles != null) {
            for (AssignmentFile assignmentFile : assignmentFiles) {
                AssignmentFileResponse assignmentFileResponse = AssignmentFileResponse.builder()
                        .fileName(assignmentFile.getFileName())
                        .fileType(assignmentFile.getFileType())
                        .filePath(assignmentFile.getFilePath())
                        .fileSize(assignmentFile.getFileSize())
                        .uploadedAt(assignmentFile.getUploadedAt())
                        .build();
                fileResponses.add(assignmentFileResponse);
            }
        }

        String status;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(assignment.getStartAt())) {
            status = "Chưa bắt đầu";
        } else if (now.isAfter(assignment.getEndAt())) {
            status = "Đã hết hạn";
        } else {
            status = "Đang diễn ra";
        }

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .classId(assignment.getClassEntity().getId())
                .files(fileResponses)
                .status(status)
                .startAt(assignment.getStartAt())
                .endAt(assignment.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .build();
    }


    public AssignmentResponse updateAssignment (AssignmentUpdateRequest request, Long id) throws IOException {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        if(request.getTitle() != null) assignment.setTitle(request.getTitle());
        if(request.getContent() != null) assignment.setContent(request.getContent());
        if(request.getStartAt() != null) assignment.setStartAt(request.getStartAt());
        if(request.getEndAt() != null) assignment.setEndAt(request.getEndAt());
        if(request.getFiles() != null && !request.getFiles().isEmpty()){
            assignmentFileService.saveFile(request.getFiles(), assignment);
        }
        assignment = assignmentRepository.save(assignment);
        List<AssignmentFileResponse> fileResponses = assignment.getAssignmentFiles()
                .stream()
                .map(file -> AssignmentFileResponse.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .filePath(file.getFilePath())
                        .fileSize(file.getFileSize())
                        .uploadedAt(file.getUploadedAt())
                        .build())
                .toList();
        String status;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(assignment.getStartAt())) {
            status = "Chưa bắt đầu";
        } else if (now.isAfter(assignment.getEndAt())) {
            status = "Đã hết hạn";
        } else {
            status = "Đang diễn ra";
        }
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .classId(assignment.getClassEntity().getId())
                .files(fileResponses)
                .status(status)
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .build();
    }

    public void deleteAssignmentById(Long id){
        if(!assignmentRepository.existsById(id)){
            throw new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }
        assignmentRepository.deleteById(id);
    }
 }
