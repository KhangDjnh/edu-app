package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.repository.FileRecordRepository;
import com.khangdjnh.edu_app.service.CloudflareR2Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cloud-files")
public class FileController {

    private final CloudflareR2Service r2Service;

    public FileController(CloudflareR2Service r2Service, FileRecordRepository fileRecordRepository) {
        this.r2Service = r2Service;
    }

    @PostMapping("/upload")
    public ApiResponse<?> upload(@RequestParam("file") MultipartFile file) {
        return r2Service.uploadFileToS3(file);
    }

    @GetMapping("/{fileId}")
    public ApiResponse<?> getFile(@PathVariable Long fileId) {
        return r2Service.getFileFromS3(fileId);
    }
}