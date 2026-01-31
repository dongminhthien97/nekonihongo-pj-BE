// HiraganaMapper.java
package com.nekonihongo.backend.mapper;

import com.nekonihongo.backend.dto.HiraganaDTO;
import com.nekonihongo.backend.dto.request.HiraganaRequest;
import com.nekonihongo.backend.entity.Hiragana;
import org.springframework.stereotype.Component;

@Component
public class HiraganaMapper {

    public HiraganaDTO toDTO(Hiragana hiragana) {
        if (hiragana == null) {
            return null;
        }

        return HiraganaDTO.builder()
                .id(hiragana.getId())
                .character(hiragana.getCharacter())
                .romanji(hiragana.getRomanji())
                .unicode(hiragana.getUnicode())
                .strokeOrder(hiragana.getStrokeOrder())
                .createdAt(hiragana.getCreatedAt())
                .updatedAt(hiragana.getUpdatedAt())
                .build();
    }

    public Hiragana toEntity(HiraganaDTO dto) {
        if (dto == null) {
            return null;
        }

        return Hiragana.builder()
                .id(dto.getId())
                .character(dto.getCharacter())
                .romanji(dto.getRomanji())
                .unicode(dto.getUnicode())
                .strokeOrder(dto.getStrokeOrder())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public Hiragana toEntity(HiraganaRequest request) {
        if (request == null) {
            return null;
        }

        return Hiragana.builder()
                .character(request.getCharacter())
                .romanji(request.getRomanji())
                .unicode(request.getUnicode())
                .strokeOrder(request.getStrokeOrder())
                .build();
    }
}