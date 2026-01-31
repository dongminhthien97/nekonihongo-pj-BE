// src/main/java/com/nekonihongo/backend/entity/JlptLevel.java
package com.nekonihongo.backend.entity;

import com.nekonihongo.backend.enums.JlptLevelType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jlpt_level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JlptLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private JlptLevelType level;

    @Column(nullable = false)
    private String displayName;
}