package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "learning_roadmaps")
public class LearningRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity classEntity;

    String title;

    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    FileRecord fileRecord;

    @Column(name = "background_image")
    String backgroundImage;

    @Column(name = "icon_image")
    String iconImage;

    @Column(name = "roadmap_index")
    Integer roadmapIndex;

    @Column(name = "parent_id")
    Long parentId;

    @Column(name = "created_by")
    String createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
}
