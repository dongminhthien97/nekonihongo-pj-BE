// src/main/java/com/nekonihongo/backend/entity/KanjiCompound.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kanji_compounds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanjiCompound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanji_id", nullable = false)
    private Kanji kanji;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String reading;

    @Column(nullable = false)
    private String meaning;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}