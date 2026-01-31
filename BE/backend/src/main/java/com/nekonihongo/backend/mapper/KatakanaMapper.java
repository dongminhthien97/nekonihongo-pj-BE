// KatakanaMapper.java
package com.nekonihongo.backend.mapper;

import com.nekonihongo.backend.dto.KatakanaDTO;
import com.nekonihongo.backend.dto.request.KatakanaRequest;
import com.nekonihongo.backend.entity.Katakana;
import org.springframework.stereotype.Component;

@Component
public class KatakanaMapper {

    public KatakanaDTO toDTO(Katakana katakana) {
        if (katakana == null) {
            return null;
        }

        return KatakanaDTO.builder()
                .id(katakana.getId())
                .character(katakana.getCharacter())
                .romanji(katakana.getRomanji())
                .unicode(katakana.getUnicode())
                .strokeOrder(katakana.getStrokeOrder())
                .createdAt(katakana.getCreatedAt())
                .updatedAt(katakana.getUpdatedAt())
                .build();
    }

    public Katakana toEntity(KatakanaDTO dto) {
        if (dto == null) {
            return null;
        }

        return Katakana.builder()
                .id(dto.getId())
                .character(dto.getCharacter())
                .romanji(dto.getRomanji())
                .unicode(dto.getUnicode())
                .strokeOrder(dto.getStrokeOrder())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public Katakana toEntity(KatakanaRequest request) {
        if (request == null) {
            return null;
        }

        return Katakana.builder()
                .character(request.getCharacter())
                .romanji(request.getRomanji())
                .unicode(request.getUnicode())
                .strokeOrder(request.getStrokeOrder())
                .build();
    }
}