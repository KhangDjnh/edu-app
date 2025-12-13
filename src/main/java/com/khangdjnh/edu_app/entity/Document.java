package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.http.entity.FileEntity;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity classEntity;

    @Column(nullable = false)
    String title;

    @Builder.Default
    @Column(name = "file_path")
    String filePath = "https://docs.github.com/en";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    FileRecord fileRecord;

    @Column(name = "uploaded_by", nullable = false)
    String uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    @CreationTimestamp
    LocalDateTime uploadedAt;

}
