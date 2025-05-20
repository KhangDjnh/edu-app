package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.entity.Assignment;
import com.khangdjnh.edu_app.entity.AssignmentFile;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.AssignmentFileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class AssignmentFileService {
    final AssignmentFileRepository assignmentFileRepository;

    @Value("${file.upload-dir}")
    String uploadDir;

    public void saveFile(List<MultipartFile> files, Assignment assignment) throws IOException {
        // Tạo thư mục nếu chưa có
        Path uploadPath = Paths.get(uploadDir);
        log.info("uploadPath: " + uploadPath.toAbsolutePath());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // Lưu file vào local
        for(MultipartFile file : files) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Lưu thông tin file vào DB
            AssignmentFile assignmentFile = AssignmentFile.builder()
                    .assignment(assignment)
                    .fileName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            assignmentFileRepository.save(assignmentFile);
        }
    }

    public AssignmentFile getFileById(Long fileId) {
        if(!assignmentFileRepository.existsById(fileId)) throw new AppException((ErrorCode.FILE_NOT_FOUND));
        return assignmentFileRepository.findByIdAndIsDeletedFalse(fileId);
    }

    public void softDeleteFile(Long fileId) {
        if(!assignmentFileRepository.existsById(fileId)) throw new AppException((ErrorCode.FILE_NOT_FOUND));
        AssignmentFile file = assignmentFileRepository.findByIdAndIsDeletedFalse(fileId);

        file.setIsDeleted(true);
        assignmentFileRepository.save(file);
    }
}
