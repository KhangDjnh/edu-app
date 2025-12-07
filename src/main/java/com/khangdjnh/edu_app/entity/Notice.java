package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.EntityType;
import com.khangdjnh.edu_app.enums.NoticeType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    User receiver;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "readed", nullable = false)
    Boolean read = false;

    @Column(name = "sender_user_name", nullable = false)
    String senderUserName;

    @Column(name = "sender_user_email", nullable = false)
    String senderUserEmail;

    @Column(name = "sender_user_full_name")
    String senderUserFullName;

    @Enumerated(EnumType.STRING)
    NoticeType type;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityType entityType;

    @Column(name = "entity_id")
    Long entityId;
}


