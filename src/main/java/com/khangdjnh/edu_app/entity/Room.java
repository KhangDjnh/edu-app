package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.PrimarySubject;
import com.khangdjnh.edu_app.enums.RoomStatus;
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
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "room_code", nullable = false)
    String roomCode;

    @Column(name = "room_name", nullable = false)
    String roomName;

    @Column(name = "teacher_id", nullable = false)
    Long teacherId;

    @Column(name = "class_id", nullable = false)
    Long classId;

    @Column(name = "subject")
    PrimarySubject subject;

    @Column(name = "start_time")
    LocalDateTime startTime;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    RoomStatus status;

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "created_by")
    String createdBy;
}
