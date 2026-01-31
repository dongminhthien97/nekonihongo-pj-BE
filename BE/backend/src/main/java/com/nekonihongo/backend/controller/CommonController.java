package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.CategoryDTO;
import com.nekonihongo.backend.dto.JlptLevelDTO;
import com.nekonihongo.backend.service.CategoryService;
import com.nekonihongo.backend.service.JlptLevelService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommonController {

    private final CategoryService categoryService;
    private final JlptLevelService jlptLevelService;

    @GetMapping("/categories")
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/levels")
    public List<JlptLevelDTO> getLevels() {
        return jlptLevelService.getAllLevels();
    }
}