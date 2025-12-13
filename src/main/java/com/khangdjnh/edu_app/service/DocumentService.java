package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.document.DocumentCreateRequest;
import com.khangdjnh.edu_app.dto.request.document.DocumentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.DocumentResponse;
import com.khangdjnh.edu_app.dto.response.FileRecordResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Document;
import com.khangdjnh.edu_app.entity.FileRecord;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.DocumentMapper;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.DocumentRepository;
import com.khangdjnh.edu_app.util.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentService {
    DocumentRepository documentRepository;
    ClassRepository classRepository;
    DocumentMapper documentMapper;
    CloudflareR2Service cloudflareR2Service;

    //Create document
    @Transactional(rollbackFor = Exception.class)
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        FileRecord fileRecord = cloudflareR2Service.uploadFileV2(request.getFile());
        Document document = Document.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .filePath(request.getFilePath())
                .fileRecord(fileRecord)
                .uploadedBy(SecurityUtils.getCurrentUserFullName())
                .build();
        document = documentRepository.save(document);
        return toDocumentResponse(document);
    }
    //Get document by id
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        return toDocumentResponse(document);
    }

    //Get all documents
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream().map(this::toDocumentResponse).toList();
    }

    //Get documents in class
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsInClass(Long classId){
        return documentRepository.findByClassEntityId(classId).stream().map(this::toDocumentResponse).toList();
    }

    //Update document by id
    @Transactional(rollbackFor = Exception.class)
    public DocumentResponse updateDocumentById(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        documentMapper.updateDocumentFromRequest(document, request);
        document = documentRepository.save(document);
        return toDocumentResponse(document);
    }

    //Delete document by id
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocumentById(Long id) {
        if(!documentRepository.existsById(id)) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        documentRepository.deleteById(id);
    }

    private DocumentResponse toDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .filePath(document.getFilePath())
                .uploadedBy(document.getUploadedBy())
                .fileRecord(document.getFileRecord() != null ?
                        FileRecordResponse.builder()
                                .id(document.getFileRecord().getId())
                                .fileName(document.getFileRecord().getFileName())
                                .fileSize(document.getFileRecord().getFileSize())
                                .fileType(document.getFileRecord().getFileType())
                                .fileUrl(document.getFileRecord().getFileUrl())
                                .build() : null)
                .uploadedAt(document.getUploadedAt())
                .build();
    }

}
