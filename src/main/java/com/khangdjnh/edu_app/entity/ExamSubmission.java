package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exam_submissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"exam_id", "student_id"})
        }
)
public class ExamSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "submission_file", nullable = false)
    private String submissionFile;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "submitted_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime submittedAt;

}