// GrammarPoint.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Builder.Default;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grammar_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class GrammarPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private GrammarLesson lesson;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String meaning;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String explanation;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0; // mặc định 0

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<GrammarExample> examples = new ArrayList<>();
}