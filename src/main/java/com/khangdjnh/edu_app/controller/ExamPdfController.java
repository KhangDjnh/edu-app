package com.khangdjnh.edu_app.controller;

import com.khangdjnh.edu_app.service.ExamPdfService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam-pdf")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamPdfController {
    ExamPdfService examPdfService;

    @GetMapping("/exams/{examId}/pdf")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<byte[]> getExamPdf(@PathVariable Long examId) {
        byte[] pdf = examPdfService.generateExamPdf(examId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=exam_" + examId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
