// src/main/java/com/nekonihongo/backend/entity/GrammarPattern.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grammar_pattern")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrammarPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String level; // "N5", "N4", "N3", "N2", "N1"

    @Column(nullable = false, length = 300)
    private String pattern; // "~ は ~"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning; // "thì, là, ở"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String example; // "山田（やまだ）さんは日本語（にほんご）が上手（じょうず）です。"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String exampleMeaning; // "Anh Yamada giỏi tiếng Nhật"
}