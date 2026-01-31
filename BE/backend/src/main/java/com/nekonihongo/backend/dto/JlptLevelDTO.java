// src/main/java/com/nekonihongo/backend/dto/JlptLevelDTO.java
package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JlptLevelDTO {
    private Integer id;
    private String level; // "N5", "N4", "N3", "N2", "N1"
    private String displayName; // "JLPT N5", "JLPT N4", ...
}