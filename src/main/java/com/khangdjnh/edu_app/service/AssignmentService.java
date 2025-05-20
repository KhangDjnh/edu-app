package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.assignments.AssignmentCreateRequest;
import com.khangdjnh.edu_app.dto.response.AssignmentFileResponse;
import com.khangdjnh.edu_app.dto.response.AssignmentResponse;
import com.khangdjnh.edu_app.entity.Assignment;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AssignmentRepository;
import com.khangdjnh.edu_app.repository.ClassRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignmentService {
    final AssignmentRepository assignmentRepository;
    final AssignmentFileService assignmentFileService;
    final ClassRepository classRepository;

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
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .classId(request.getClassId())
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

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .content(assignment.getContent())
                .classId(assignment.getClassEntity().getId())
                .startAt(assignment.getStartAt())
                .endAt(assignment.getEndAt())
                .createdAt(assignment.getCreatedAt())
                .files(fileResponses)
                .build();
    }

}
