// KatakanaService.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.KatakanaDTO;
import com.nekonihongo.backend.dto.request.KatakanaRequest;
import com.nekonihongo.backend.entity.Katakana;
import com.nekonihongo.backend.mapper.KatakanaMapper;
import com.nekonihongo.backend.repository.KatakanaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KatakanaService {
    private final KatakanaRepository katakanaRepository;
    private final KatakanaMapper katakanaMapper;

    public List<KatakanaDTO> getAllKatakana() {
        return katakanaRepository.findAll()
                .stream()
                .map(katakanaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public KatakanaDTO getByCharacter(String character) {
        Katakana katakana = katakanaRepository.findByCharacter(character)
                .orElse(null);
        return katakanaMapper.toDTO(katakana);
    }

    public KatakanaDTO createKatakana(KatakanaRequest request) {
        // Check if character already exists
        if (katakanaRepository.findByCharacter(request.getCharacter()).isPresent()) {
            throw new RuntimeException("Katakana character already exists");
        }

        Katakana katakana = katakanaMapper.toEntity(request);
        Katakana saved = katakanaRepository.save(katakana);
        return katakanaMapper.toDTO(saved);
    }

    public KatakanaDTO updateKatakana(Integer id, KatakanaRequest request) {
        Katakana existing = katakanaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Katakana not found with id: " + id));

        // Check if character is being changed and already exists
        if (!existing.getCharacter().equals(request.getCharacter()) &&
                katakanaRepository.findByCharacter(request.getCharacter()).isPresent()) {
            throw new RuntimeException("Katakana character already exists");
        }

        existing.setCharacter(request.getCharacter());
        existing.setRomanji(request.getRomanji());
        existing.setUnicode(request.getUnicode());
        existing.setStrokeOrder(request.getStrokeOrder());

        Katakana updated = katakanaRepository.save(existing);
        return katakanaMapper.toDTO(updated);
    }

    public void deleteKatakana(Integer id) {
        if (!katakanaRepository.existsById(id)) {
            throw new RuntimeException("Katakana not found with id: " + id);
        }
        katakanaRepository.deleteById(id);
    }
}