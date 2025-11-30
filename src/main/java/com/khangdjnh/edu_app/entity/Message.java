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
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sender", nullable = false)
    Long sender;

    @Column(name = "conversation_id", nullable = false)
    Long conversationId;

    @Column(name = "content", nullable = false)
    String content;

    @Column(name = "attach_file_id")
    Long attachFileId;

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
