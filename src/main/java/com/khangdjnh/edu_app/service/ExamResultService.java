package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.response.ExamSubmissionResultResponse;
import com.khangdjnh.edu_app.repository.ExamSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamResultService {
    private final ExamSubmissionRepository submissionRepository;

    public List<ExamSubmissionResultResponse> getResults(Long examId) {
        return submissionRepository.findAllByExamId(examId)
                .stream()
                .map(s -> ExamSubmissionResultResponse.builder()
                        .studentId(s.getStudent().getId())
                        .studentName(s.getStudent().getFirstName() + " " + s.getStudent().getLastName())
                        .score(s.getScore())
                        .submittedAt(s.getCompletedAt())
                        .status(s.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
