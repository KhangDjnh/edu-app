package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.ClassType;
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
@Table(name = "classes")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(name = "class_code")
    String classCode;

    @Column(nullable = false)
    String semester;

    @Column(nullable = false, unique = true)
    String code;

    @Column(columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    User teacher;

    @Column(name = "class_type")
    @Enumerated(EnumType.STRING)
    ClassType classType;

    @Column(name = "power_by")
    String powerBy;

    @Column(name = "class_introduction", columnDefinition = "TEXT")
    String classIntroduction;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;
}
