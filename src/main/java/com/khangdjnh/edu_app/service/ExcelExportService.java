package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.StudentScoreRow;
import com.khangdjnh.edu_app.dto.ExamInfo;
import com.khangdjnh.edu_app.dto.response.ClassScoreSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    public void exportScoreToExcel(ClassScoreSummaryResponse summary, HttpServletResponse response) throws IOException {
        // Tạo workbook và sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Scores");

        // Style cho header
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // ==== GHI HEADER ====
        Row header = sheet.createRow(0);
        int colIdx = 0;
        header.createCell(colIdx++).setCellValue("Mã sinh viên");
        header.createCell(colIdx++).setCellValue("Họ tên");
        header.createCell(colIdx++).setCellValue("Ngày sinh");

        List<ExamInfo> exams = summary.getExams();
        for (ExamInfo exam : exams) {
            Cell cell = header.createCell(colIdx++);
            cell.setCellValue(exam.getName());
            cell.setCellStyle(headerStyle);
        }

        Cell avgCell = header.createCell(colIdx);
        avgCell.setCellValue("Điểm trung bình");
        avgCell.setCellStyle(headerStyle);

        // ==== GHI DỮ LIỆU HỌC SINH ====
        int rowIdx = 1;
        for (StudentScoreRow student : summary.getStudents()) {
            Row row = sheet.createRow(rowIdx++);
            int cellIdx = 0;

            row.createCell(cellIdx++).setCellValue(student.getStudentId());
            row.createCell(cellIdx++).setCellValue(student.getFullName());
            row.createCell(cellIdx++).setCellValue(student.getDob().toString()); // Chuyển LocalDate -> String

            Map<Long, BigDecimal> scores = student.getScores();
            for (ExamInfo exam : exams) {
                BigDecimal score = scores.getOrDefault(exam.getExamId(), null);
                if (score != null) {
                    row.createCell(cellIdx++).setCellValue(score.doubleValue());
                } else {
                    row.createCell(cellIdx++).setCellValue("");
                }
            }

            row.createCell(cellIdx).setCellValue(student.getAverageScore().doubleValue());
        }

        // Auto-size cột
        for (int i = 0; i <= exams.size() + 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // ==== GHI VỀ HTTP RESPONSE ====
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "scores.xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
