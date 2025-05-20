package com.khangdjnh.edu_app.service;

import com.khangdjnh.edu_app.dto.request.submission.GradeRequest;
import com.khangdjnh.edu_app.dto.request.submission.SubmissionRequest;
import com.khangdjnh.edu_app.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse createSubmission(SubmissionRequest request);

    SubmissionResponse getSubmissionById(Long id);

    SubmissionResponse getSubmissionsByAssignmentAndStudent(Long assignmentId, Long studentId);

    List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId);

    SubmissionResponse gradeSubmission(Long id, GradeRequest request);

    void deleteSubmission(Long id);
}