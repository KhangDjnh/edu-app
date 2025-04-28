package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.DocumentCreateRequest;
import com.khangdjnh.edu_app.dto.request.DocumentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.DocumentResponse;
import com.khangdjnh.edu_app.entity.ClassEntity;
import com.khangdjnh.edu_app.entity.Document;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.mapper.DocumentMapper;
import com.khangdjnh.edu_app.repository.ClassRepository;
import com.khangdjnh.edu_app.repository.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentService {
    DocumentRepository documentRepository;
    ClassRepository classRepository;
    DocumentMapper documentMapper;

    //Create document
    public DocumentResponse createDocument(DocumentCreateRequest request) {
        ClassEntity classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Document document = Document.builder()
                .classEntity(classEntity)
                .title(request.getTitle())
                .filePath(request.getFilePath())
                .build();
        documentRepository.save(document);
        return documentMapper.toDocumentResponse(document);
    }
    //Get document by id
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        return documentMapper.toDocumentResponse(document);
    }

    //Get all documents
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream().map(documentMapper::toDocumentResponse).toList();
    }

    //Get documents in class
    public List<DocumentResponse> getDocumentsInClass(Long classId){
        return documentRepository.findByClassEntityId(classId).stream().map(documentMapper::toDocumentResponse).toList();
    }

    //Update document by id
    public DocumentResponse updateDocumentById(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        documentMapper.updateDocumentFromRequest(document, request);
        return documentMapper.toDocumentResponse(documentRepository.save(document));
    }

    //Delete document by id
    public void deleteDocumentById(Long id) {
        if(!documentRepository.existsById(id)) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        documentRepository.deleteById(id);
    }

}
