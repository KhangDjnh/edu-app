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
@Table(name = "survey_options")
public class SurveyOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "survey_id", nullable = false)
    Long surveyId;

    @Column(name = "option_title", nullable = false)
    String optionTitle;

    @Column(name = "option_index", nullable = false)
    Integer optionIndex;

    @Column(name = "description")
    String description;
}
