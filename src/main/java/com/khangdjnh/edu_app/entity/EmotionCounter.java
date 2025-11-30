package com.khangdjnh.edu_app.entity;

import com.khangdjnh.edu_app.enums.Emotion;
import com.khangdjnh.edu_app.enums.ParentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "emotion_counter")
public class EmotionCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "parent_id", nullable = false)
    Long parentId;

    @Column(name = "parent_type", nullable = false)
    ParentType parentType;

    @Column(name = "emotion", nullable = false)
    Emotion emotion;

    @Column(name = "quantity")
    Long quantity;
}
