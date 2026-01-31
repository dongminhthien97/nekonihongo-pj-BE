// src/main/java/com/nekonihongo/backend/entity/JlptVocabulary.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jlpt_vocabulary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JlptVocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "level")
    private String level; // "N5", "N4", "N3", "N2", "N1" – không default

    @Column(nullable = false, name = "stt")
    private String stt; // số thứ tự

    @Column(nullable = false, name = "tuVung")
    private String tuVung; // hiragana/katakana

    @Column(name = "hanTu")
    private String hanTu; // kanji (có thể null)

    @Column(nullable = false, name = "tiengViet")
    private String tiengViet;

    @Column(name = "viDu")
    private String viDu; // ví dụ câu (có thể null)
}