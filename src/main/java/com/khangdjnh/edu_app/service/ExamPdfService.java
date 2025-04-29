package com.khangdjnh.edu_app.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.khangdjnh.edu_app.entity.Exam;
import com.khangdjnh.edu_app.entity.ExamQuestion;
import com.khangdjnh.edu_app.exception.AppException;
import com.khangdjnh.edu_app.exception.ErrorCode;
import com.khangdjnh.edu_app.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamPdfService {

    private final ExamRepository examRepository;

    public byte[] generateExamPdf(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        List<ExamQuestion> questions = exam.getQuestions();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont vietnameseFont = PdfFontFactory.createFont(
                    ResourceUtils.getFile("classpath:fonts/Roboto-Regular.ttf").getAbsolutePath()
            );
            document.setFont(vietnameseFont);

            document.add(new Paragraph("Đề thi: " + exam.getTitle()));
            document.add(new Paragraph("Thời gian: " + exam.getStartTime() + " - " + exam.getEndTime()));
            document.add(new Paragraph("Mô tả: " + (exam.getDescription() == null ? "" : exam.getDescription())));
            document.add(new Paragraph("\n"));

            int index = 1;
            for (ExamQuestion q : questions) {
                document.add(new Paragraph(index++ + ". " + q.getQuestion()));
                document.add(new Paragraph("A. " + q.getOptionA()));
                document.add(new Paragraph("B. " + q.getOptionB()));
                document.add(new Paragraph("C. " + q.getOptionC()));
                document.add(new Paragraph("D. " + q.getOptionD()));
                document.add(new Paragraph("\n"));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new AppException(ErrorCode.FAILED_TO_GENERATE_PDF);
        }
    }
}