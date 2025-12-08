package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.service.CloudflareR2Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final CloudflareR2Service r2Service;

    public FileController(CloudflareR2Service r2Service) {
        this.r2Service = r2Service;
    }

    @PostMapping("/images/upload")
    public ApiResponse<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return r2Service.uploadFileToS3(file);
    }

    @PostMapping("/upload")
    public ApiResponse<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return ApiResponse.builder()
                .code(1000)
                .message("success")
                .result(r2Service.uploadFileToS3(file))
                .build();
    }

    @GetMapping("/{fileId}")
    public ApiResponse<?> getFileInfo(@PathVariable Long fileId) {
        return r2Service.getFileInfo(fileId);
    }

    @GetMapping("/get")
    public ResponseEntity<ByteArrayResource> getFileFromS3(@RequestParam String fileUrl){
        return r2Service.getFileFromR2(fileUrl);
    }
}