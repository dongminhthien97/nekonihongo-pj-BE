package com.nekonihongo.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nekonihongo.backend.repository.CategoryRepository;
import com.nekonihongo.backend.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> CategoryDTO.builder()
                        .id(cat.getId())
                        .name(cat.getName().name()) // enum → String
                        .displayName(cat.getDisplayName())
                        .description(cat.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}