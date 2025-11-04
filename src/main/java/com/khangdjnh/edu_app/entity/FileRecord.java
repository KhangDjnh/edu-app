package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;

    private String uploadedBy;
    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
