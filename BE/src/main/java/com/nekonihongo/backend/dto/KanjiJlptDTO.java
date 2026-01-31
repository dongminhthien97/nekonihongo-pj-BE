// src/main/java/com/nekonihongo/backend/dto/KanjiJlptDTO.java
package com.nekonihongo.backend.dto;

import com.nekonihongo.backend.enums.JlptLevelType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanjiJlptDTO {
    private Long id;
    private String stt;
    private String kanji;
    private String hanViet;
    private String meaning;
    private String onYomi;
    private String kunYomi;
    private JlptLevelType level; // THÊM CÁI NÀY
}