package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.config.CloudflareR2Properties;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.FileRecordRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URLConnection;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class CloudflareR2Service {

    private final S3Client s3;
    private final String bucket;
    private final String endpoint;
    private final FileRecordRepository fileRecordRepository;

    public CloudflareR2Service(S3Client s3, CloudflareR2Properties props, FileRecordRepository fileRecordRepository) {
        this.s3 = s3;
        this.bucket = props.getBucket();
        this.endpoint = props.getEndpoint();
        this.fileRecordRepository = fileRecordRepository;
    }

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
    );

    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".csv"
    );

    @Transactional(readOnly = true)
    public ApiResponse<?> getFileFromS3(Long fileId) {
        FileRecord rec = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        return ApiResponse.builder()
                .message("Success")
                .result(rec)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<?> uploadFileToS3(MultipartFile file) {
        try {
            String url = uploadFile(file, "images");
            // Lưu vào DB
            FileRecord rec = new FileRecord();
            rec.setFileName(file.getOriginalFilename());
            rec.setFileUrl(url);
            rec.setFileType(getMimeType(file.getOriginalFilename()));
            rec.setFileSize(file.getSize());
            rec.setUploadedBy(SecurityUtils.getCurrentUsername());
            fileRecordRepository.save(rec);
            return ApiResponse.builder()
                    .result(url)
                    .code(1000)
                    .message("Upload success")
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .message("error: "+ e.getMessage())
                    .code(1100)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public FileRecordResponse uploadFile(MultipartFile file) {
        try {
            // 1. Xác định folder dựa trên loại file
            String folder = determineFolder(file.getOriginalFilename());

            // 2. Upload lên R2
            String fileUrl = uploadToR2(file, folder);

            // 3. Lưu vào DB
            FileRecord record = FileRecord.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .fileType(getMimeType(file.getOriginalFilename()))
                    .fileSize(file.getSize())
                    .uploadedBy(SecurityUtils.getCurrentUsername())
                    .folder(folder)
                    .build();

            record = fileRecordRepository.save(record);
            return FileRecordResponse.builder()
                    .id(record.getId())
                    .fileUrl(fileUrl)
                    .fileName(file.getOriginalFilename())
                    .fileType(getMimeType(file.getOriginalFilename()))
                    .fileSize(file.getSize())
                    .folder(folder)
                    .uploadedBy(SecurityUtils.getCurrentUsername())
                    .uploadedAt(record.getUploadedAt())
                    .build();

        } catch (Exception e) {
            log.error("Upload file thất bại: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public FileRecord uploadFileV2(MultipartFile file) {
        try {
            // 1. Xác định folder dựa trên loại file
            String folder = determineFolder(file.getOriginalFilename());

            // 2. Upload lên R2
            String fileUrl = uploadToR2(file, folder);

            // 3. Lưu vào DB
            FileRecord record = FileRecord.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .fileType(getMimeType(file.getOriginalFilename()))
                    .fileSize(file.getSize())
                    .uploadedBy(SecurityUtils.getCurrentUsername())
                    .folder(folder)
                    .build();

            record = fileRecordRepository.save(record);
            return record;
        } catch (Exception e) {
            log.error("Upload file thất bại: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UPLOAD_FILE_FAIL);
        }
    }

    @Transactional(readOnly = true)
    public ApiResponse<?> getFileInfo(Long fileId) {
        FileRecord record = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        return ApiResponse.builder()
                .code(1000)
                .message("Thành công")
                .result(record)
                .build();
    }

    private String determineFolder(String originalFilename) {
        if (originalFilename == null) {
            return "others";
        }

        String ext = getFileExtension(originalFilename).toLowerCase();

        if (IMAGE_EXTENSIONS.contains(ext)) {
            return "images";
        } else if (DOCUMENT_EXTENSIONS.contains(ext)) {
            return "documents";
        } else {
            return "others";
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private String uploadToR2(MultipartFile file, String folder) throws IOException {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "unknown-file-" + UUID.randomUUID();
        }

        String extension = getFileExtension(originalName);
        String key = folder + "/" +
                Instant.now().getEpochSecond() + "-" +
                UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Trả về URL công khai (bucket phải public)
        return String.format("%s/%s/%s", endpoint.trim(), bucket, key);
    }

    private String getMimeType(String originalFilename) {
        if (originalFilename == null) {
            return "application/octet-stream"; // default binary
        }
        String mimeType = URLConnection.guessContentTypeFromName(originalFilename);
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    private String uploadFile(MultipartFile file, String folder) throws Exception {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String key = folder + "/" + Instant.now().getEpochSecond() + "-" + UUID.randomUUID() + ext;

        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Nếu bucket public-visible: có thể build URL trực tiếp
        // URL pattern: https://<ACCOUNT_ID>.r2.cloudflarestorage.com/<bucket>/<key>
        return String.format("%s/%s/%s", endpoint, bucket, key);
    }
}

