// src/main/java/com/nekonihongo/backend/dto/kanji/KanjiLessonDto.java
package com.nekonihongo.backend.dto.kanji;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KanjiLessonDto {
    private Integer id;
    private String title;
    private String icon;
    private List<KanjiDto> kanjiList;
}