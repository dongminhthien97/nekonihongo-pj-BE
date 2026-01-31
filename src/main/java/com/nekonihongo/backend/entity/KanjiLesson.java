// src/main/java/com/nekonihongo/backend/entity/KanjiLesson.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kanji_lessons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanjiLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    @Builder.Default
    private String icon = "Cat";

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "lesson", cascade = { CascadeType.ALL }, // ← Fix: thêm {} để thành array
            orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<Kanji> kanjiList = new ArrayList<>();
}