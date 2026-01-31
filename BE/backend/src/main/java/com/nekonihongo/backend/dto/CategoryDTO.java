// src/main/java/com/nekonihongo/backend/dto/CategoryDTO.java
package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name; // "VOCABULARY", "GRAMMAR", "KANJI"
    private String displayName; // "Từ vựng", "Ngữ pháp", "Kanji"
    private String description; // Mô tả chi tiết
}