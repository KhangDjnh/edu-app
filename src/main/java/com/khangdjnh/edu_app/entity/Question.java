package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.enums.QuestionLevel;
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
@Table(name = "exam_questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity classEntity;

    @Column(nullable = false, columnDefinition = "TEXT")
    String question;

    @Column(name = "option_a", nullable = false)
    String optionA;

    @Column(name = "option_b", nullable = false)
    String optionB;

    @Column(name = "option_c", nullable = false)
    String optionC;

    @Column(name = "option_d", nullable = false)
    String optionD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AnswerOption answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    QuestionLevel level;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;
}