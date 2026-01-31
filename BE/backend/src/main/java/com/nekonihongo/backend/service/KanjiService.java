// src/main/java/com/nekonihongo/backend/service/KanjiService.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.KanjiJlptDTO;
import com.nekonihongo.backend.entity.KanjiJlpt;
import com.nekonihongo.backend.enums.JlptLevelType;
import com.nekonihongo.backend.repository.KanjiJlptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KanjiService {

    private final KanjiJlptRepository kanjiJlptRepository;

    // Lấy tất cả kanji theo cấp độ JLPT
    public List<KanjiJlptDTO> getKanjiByLevel(JlptLevelType level) {
        List<KanjiJlpt> kanjiList = kanjiJlptRepository.findByLevelOrderBySttAsc(level);

        return kanjiList.stream()
                .map(k -> KanjiJlptDTO.builder()
                        .id(k.getId())
                        .stt(k.getStt())
                        .kanji(k.getKanji())
                        .hanViet(k.getHanViet() != null ? k.getHanViet() : "-")
                        .meaning(k.getMeaning())
                        .onYomi(k.getOnYomi() != null ? k.getOnYomi() : "-")
                        .kunYomi(k.getKunYomi() != null ? k.getKunYomi() : "-")
                        .level(k.getLevel())
                        .build())
                .toList();
    }

    // Lấy tất cả kanji của tất cả cấp độ
    public List<KanjiJlptDTO> getAllJlptKanji() {
        List<KanjiJlpt> kanjiList = kanjiJlptRepository.findAllByOrderByLevelAscSttAsc();

        return kanjiList.stream()
                .map(k -> KanjiJlptDTO.builder()
                        .id(k.getId())
                        .stt(k.getStt())
                        .kanji(k.getKanji())
                        .hanViet(k.getHanViet() != null ? k.getHanViet() : "-")
                        .meaning(k.getMeaning())
                        .onYomi(k.getOnYomi() != null ? k.getOnYomi() : "-")
                        .kunYomi(k.getKunYomi() != null ? k.getKunYomi() : "-")
                        .level(k.getLevel())
                        .build())
                .toList();
    }

    public long getKanjiCountByLevel(JlptLevelType level) {
        return kanjiJlptRepository.countByLevel(level);
    }
}