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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Paths;
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

    public ResponseEntity<ByteArrayResource> getFileFromR2(String fileUrl) {
        try {
            // 1. Tách lấy Key từ fileUrl
            // Giả sử URL lưu trong DB là: https://endpoint/bucket/folder/filename.jpg
            // Chúng ta cần lấy phần: folder/filename.jpg
            String key = extractKeyFromUrl(fileUrl);

            // 2. Tạo Request lấy file từ R2
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            // 3. Lấy dữ liệu dưới dạng Bytes
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
            GetObjectResponse responseMeta = objectBytes.response();

            // 4. Chuẩn bị dữ liệu trả về
            ByteArrayResource resource = new ByteArrayResource(objectBytes.asByteArray());

            // 5. Xác định Content-Type (Quan trọng để hiển thị đúng)
            String contentType = responseMeta.contentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream"; // Mặc định nếu không tìm thấy
            }

            // 6. Trả về ResponseEntity
            return ResponseEntity.ok()
                    // Quan trọng: contentType giúp trình duyệt biết đây là ảnh hay pdf
                    .contentType(MediaType.parseMediaType(contentType))
                    // Quan trọng: "inline" báo cho trình duyệt mở file ngay thay vì tải về
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + extractFileName(key) + "\"")
                    .body(resource);

        } catch (Exception e) {
            // Xử lý lỗi (ví dụ file không tồn tại)
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
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

    private String extractKeyFromUrl(String url) {

        // Tìm vị trí của tên bucket trong URL
        String bucketSegment = "/" + bucket + "/"; // Kết quả: "/e-learning/"
        int index = url.indexOf(bucketSegment);

        if (index != -1) {
            // Cắt lấy phần sau "/e-learning/"
            return url.substring(index + bucketSegment.length());
        }

        return url;
    }

    private String extractFileName(String key) {
        return Paths.get(key).getFileName().toString();
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

