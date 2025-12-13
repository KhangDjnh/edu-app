package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.dto.request.document.DocumentCreateRequest;
import com.khangdjnh.edu_app.dto.request.document.DocumentUpdateRequest;
import com.khangdjnh.edu_app.dto.response.ApiResponse;
import com.khangdjnh.edu_app.dto.response.DocumentResponse;
import com.khangdjnh.edu_app.service.DocumentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DocumentController {
    DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<DocumentResponse> createDocument(@ModelAttribute @Valid DocumentCreateRequest request) {
        return ApiResponse.<DocumentResponse>builder()
                .message("Success")
                .code(1000)
                .result(documentService.createDocument(request))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<DocumentResponse> getDocumentById(@PathVariable Long id) {
        return ApiResponse.<DocumentResponse>builder()
                .message("Success")
                .code(1000)
                .result(documentService.getDocumentById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<DocumentResponse>> getAllDocuments () {
        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Success")
                .code(1000)
                .result(documentService.getAllDocuments())
                .build();
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    ApiResponse<List<DocumentResponse>> getAllDocumentsByClassId (@PathVariable Long classId) {
        return ApiResponse.<List<DocumentResponse>>builder()
                .message("Success")
                .code(1000)
                .result(documentService.getDocumentsInClass(classId))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<DocumentResponse> updateDocumentById(@PathVariable Long id,@RequestBody @Valid DocumentUpdateRequest request) {
        return ApiResponse.<DocumentResponse>builder()
                .message("Success")
                .code(1000)
                .result(documentService.updateDocumentById(id,request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    ApiResponse<String> deleteDocumentById(@PathVariable Long id) {
        documentService.deleteDocumentById(id);
        return ApiResponse.<String>builder()
                .message("Success")
                .code(1000)
                .result("Document " + id + " successfully deleted")
                .build();
    }

}
