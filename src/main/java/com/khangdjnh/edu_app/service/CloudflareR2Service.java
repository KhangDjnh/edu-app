package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.config.CloudflareR2Properties;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.repository.FileRecordRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.net.URLConnection;
import java.time.Instant;
import java.util.UUID;

@Service
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

    public ApiResponse<?> getFileFromS3(Long fileId) {
        FileRecord rec = fileRecordRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        return ApiResponse.builder()
                .message("Success")
                .result(rec)
                .build();
    }

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
