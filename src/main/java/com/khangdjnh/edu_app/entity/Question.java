package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.AnswerOption;
import com.khangdjnh.edu_app.enums.QuestionLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    @EqualsAndHashCode.Include
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    ClassEntity classEntity;

    @Column(name = "chapter", nullable = false)
    Integer chapter;

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

    @Builder.Default
    @ManyToMany(mappedBy = "questions")
    Set<Exam> exams = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Question question = (Question) o;
        return getId() != null && Objects.equals(getId(), question.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}