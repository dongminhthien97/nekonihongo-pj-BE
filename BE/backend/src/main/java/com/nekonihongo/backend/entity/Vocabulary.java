// src/main/java/com/nekonihongo/backend/entity/Vocabulary.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vocabulary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_id", nullable = false)
    private Integer lessonId;

    @Column(nullable = false, length = 255)
    private String japanese;

    @Column(length = 255)
    private String kanji = "";

    @Column(nullable = false, length = 255)
    private String vietnamese;

    @Column(length = 100)
    private String category;
}