// GrammarExample.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grammar_examples")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class GrammarExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private GrammarPoint point;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String japanese;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String vietnamese;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}