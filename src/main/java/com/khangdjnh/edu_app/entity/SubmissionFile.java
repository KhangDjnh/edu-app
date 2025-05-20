package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "submission_files")
public class SubmissionFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    Submission submission;

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "file_path", nullable = false)
    String filePath;

    @Column(name = "file_type", nullable = false)
    String fileType;

    @Column(name = "file_size", nullable = false)
    Long fileSize;

    @Column(name = "uploaded_at", nullable = false)
    LocalDateTime uploadedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    Boolean isDeleted = Boolean.TRUE;
}
