// src/main/java/com/nekonihongo/backend/dto/kanji/KanjiDto.java
package com.nekonihongo.backend.dto.kanji;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KanjiDto {
    private String kanji; // 字
    private String on; // Âm On
    private String kun; // Âm Kun (có thể null)
    private String hanViet; // Âm Hán Việt
    private String meaning; // Ý nghĩa
    private Integer strokes; // Số nét
    private List<KanjiCompoundDto> compounds; // Từ ghép phổ biến
}