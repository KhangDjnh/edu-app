package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.CloudflareR2Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cloud-files")
public class FileController {

    private final CloudflareR2Service r2Service;

    public FileController(CloudflareR2Service r2Service) {
        this.r2Service = r2Service;
    }

    @PostMapping("/images/upload")
    public ApiResponse<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return r2Service.uploadFileToS3(file);
    }

    @GetMapping("/images/{fileId}")
    public ApiResponse<?> getImageFile(@PathVariable Long fileId) {
        return r2Service.getFileFromS3(fileId);
    }

    @PostMapping("/files/upload-file")
    public ApiResponse<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return r2Service.uploadFile(file);
    }

    @GetMapping("/files/{fileId}")
    public ApiResponse<?> getFileInfo(@PathVariable Long fileId) {
        return r2Service.getFileInfo(fileId);
    }
}