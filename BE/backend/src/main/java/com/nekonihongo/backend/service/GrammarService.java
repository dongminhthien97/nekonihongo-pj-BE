// src/main/java/com/nekonihongo/backend/service/GrammarService.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.ApiResponse;
import com.nekonihongo.backend.dto.GrammarCountDTO;
import com.nekonihongo.backend.dto.GrammarPatternDTO;
import com.nekonihongo.backend.entity.GrammarPattern;
import com.nekonihongo.backend.repository.GrammarPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrammarService {

    private final GrammarPatternRepository grammarPatternRepository;

    // Lấy danh sách ngữ pháp theo level
    public List<GrammarPatternDTO> getGrammarPatternsByLevel(String level) {
        List<GrammarPattern> patterns = grammarPatternRepository.findByLevelOrderByIdAsc(level);

        return patterns.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Lấy số lượng ngữ pháp theo level
    public Long getGrammarCountByLevel(String level) {
        return grammarPatternRepository.countByLevel(level);
    }

    // Lấy tất cả ngữ pháp (tất cả level)
    public List<GrammarPatternDTO> getAllGrammarPatterns() {
        List<GrammarPattern> patterns = grammarPatternRepository.findAllByOrderByLevelAscIdAsc();

        return patterns.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Lấy số lượng ngữ pháp của tất cả các level
    public Map<String, Long> getGrammarCountsByAllLevels() {
        List<Object[]> results = grammarPatternRepository.countAllByLevelGroup();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]));
    }

    // Convert entity to DTO
    private GrammarPatternDTO convertToDTO(GrammarPattern pattern) {
        return GrammarPatternDTO.builder()
                .id(pattern.getId())
                .level(pattern.getLevel())
                .pattern(pattern.getPattern())
                .meaning(pattern.getMeaning())
                .example(pattern.getExample())
                .exampleMeaning(pattern.getExampleMeaning())
                .build();
    }

    // Phương thức hỗ trợ cho API cũ (giữ lại để tương thích)
    public List<GrammarPatternDTO> getN5GrammarPatterns() {
        return getGrammarPatternsByLevel("N5");
    }
}