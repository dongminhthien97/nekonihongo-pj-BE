// src/main/java/com/nekonihongo/backend/service/JlptVocabularyService.java
package com.nekonihongo.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nekonihongo.backend.dto.JlptVocabularyDTO;
import com.nekonihongo.backend.entity.JlptVocabulary;
import com.nekonihongo.backend.repository.JlptVocabularyRepository;

@Service
@RequiredArgsConstructor
public class JlptVocabularyService {

    private final JlptVocabularyRepository repository;

    /**
     * Lấy tất cả từ vựng theo level với phân trang
     */
    public Page<JlptVocabularyDTO> getByLevel(String level, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<JlptVocabulary> result = repository.findByLevel(level, pageable);

        return result.map(this::toDTO);
    }

    /**
     * Tìm kiếm từ vựng theo level và query (tuVung, hanTu, tiengViet)
     */
    public Page<JlptVocabularyDTO> searchByLevel(String level, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<JlptVocabulary> result = repository.searchByLevel(level, query, pageable);

        return result.map(this::toDTO);
    }

    /**
     * Lấy tổng số từ theo level
     */
    public long getCountByLevel(String level) {
        return repository.countByLevel(level);
    }

    /**
     * Lấy tất cả từ vựng (không filter level) – dùng khi cần
     */
    public Page<JlptVocabularyDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<JlptVocabulary> result = repository.findAll(pageable);

        return result.map(this::toDTO);
    }

    /**
     * Tìm kiếm toàn bộ (không filter level)
     */
    public Page<JlptVocabularyDTO> searchAll(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<JlptVocabulary> result = repository.searchAll(query, pageable);

        return result.map(this::toDTO);
    }

    /**
     * Lấy tổng số từ toàn bộ
     */
    public long getTotalCount() {
        return repository.count();
    }

    /**
     * Convert Entity → DTO
     */
    private JlptVocabularyDTO toDTO(JlptVocabulary entity) {
        return JlptVocabularyDTO.builder()
                .level(entity.getLevel())
                .stt(entity.getStt())
                .tuVung(entity.getTuVung())
                .hanTu(entity.getHanTu())
                .tiengViet(entity.getTiengViet())
                .viDu(entity.getViDu())
                .build();
    }
}