package com.khangdjnh.edu_app.entity;

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
@Table(name = "post_types")
public class PostType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "type_code", nullable = false)
    String typeCode;

    @Column(name = "type_name", nullable = false)
    String typeName;

    @Column(name = "post_title")
    String postTitle;

    @Column(name = "post_content")
    String postContent;

    @Column(name = "post_icon")
    String postIcon;

    @Column(name = "post_background")
    String postBackground;
}
