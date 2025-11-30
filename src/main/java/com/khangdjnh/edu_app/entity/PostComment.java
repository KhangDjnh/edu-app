package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.Emotion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "post_id", nullable = false)
    Long postId;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "reply_to")
    Long replyTo;

    @Column(name = "emotion")
    @Enumerated(EnumType.STRING)
    Emotion emotion;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
