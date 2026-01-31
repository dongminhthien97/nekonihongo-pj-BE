// Katakana.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "katakana")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Katakana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "`character`", nullable = false, unique = true) // Dùng backticks cho keyword
    private String character;

    @Column(name = "romanji", nullable = false)
    private String romanji;

    @Column(name = "unicode", nullable = false)
    private String unicode;

    @Column(name = "stroke_order")
    private Integer strokeOrder;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}