package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
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

    @Column(name = "file_path", nullable = false)
    String filePath;

    @Column(name = "uploaded_at", nullable = false)
    @CreationTimestamp
    LocalDateTime uploadedAt;

}
