// src/main/java/com/nekonihongo/backend/entity/Category.java
package com.nekonihongo.backend.entity;

import com.nekonihongo.backend.enums.CategoryType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private CategoryType name; // ← dùng enum từ file riêng

    @Column(nullable = false)
    private String displayName;

    private String description;
}