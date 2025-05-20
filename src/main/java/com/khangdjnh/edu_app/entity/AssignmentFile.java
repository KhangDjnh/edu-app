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
@Table(name = "assignment_files")
public class AssignmentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    Assignment assignment;

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
    Boolean isDeleted = Boolean.FALSE;
}
