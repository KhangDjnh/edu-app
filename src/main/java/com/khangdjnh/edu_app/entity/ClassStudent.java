package com.khangdjnh.edu_app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "class_students")
public class ClassStudent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity classEntity;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    User student;

    @Column(name = "present_number")
    Integer presentNumber;

    @Column(name = "late_number")
    Integer lateNumber;

    @Column(name = "absence_number")
    Integer absenceNumber;

    @Column(name = "joined_at")
    @CreationTimestamp
    LocalDateTime joinAt;
}
