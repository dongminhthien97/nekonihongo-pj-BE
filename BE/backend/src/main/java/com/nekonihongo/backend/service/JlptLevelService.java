package com.nekonihongo.backend.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nekonihongo.backend.dto.JlptLevelDTO;
import com.nekonihongo.backend.entity.JlptLevel;
import com.nekonihongo.backend.repository.JlptLevelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JlptLevelService {
    private final JlptLevelRepository jlptLevelRepository;

    public List<JlptLevelDTO> getAllLevels() {
        return jlptLevelRepository.findAll().stream()
                .sorted(Comparator.comparingInt(JlptLevel::getId).reversed()) // N1 → N5
                .map(level -> JlptLevelDTO.builder()
                        .id(level.getId())
                        .level(level.getLevel().name())
                        .displayName(level.getDisplayName())
                        .build())
                .collect(Collectors.toList());
    }
}
