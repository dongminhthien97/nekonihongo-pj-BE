// src/main/java/com/nekonihongo/backend/dto/kanji/KanjiCompoundDto.java
package com.nekonihongo.backend.dto.kanji;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KanjiCompoundDto {
    private String word; // Từ ghép: 日本
    private String reading; // Cách đọc: にほん
    private String meaning; // Nghĩa: Nhật Bản
}