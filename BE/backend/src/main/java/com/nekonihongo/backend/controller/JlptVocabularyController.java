// src/main/java/com/nekonihongo/backend/controller/JlptVocabularyController.java
package com.nekonihongo.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nekonihongo.backend.dto.JlptVocabularyDTO;
import com.nekonihongo.backend.service.JlptVocabularyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JlptVocabularyController {

    private final JlptVocabularyService jlptVocabularyService;

    @GetMapping("/{level}")
    public ResponseEntity<Map<String, Object>> getByLevel(
            @PathVariable("level") String level,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam(name = "q", required = false) String q) {

        Map<String, Object> response = new HashMap<>();
        try {
            // SỬA 1: Chấp nhận cả chữ hoa và chữ thường (n5, N5, n4, N4,...)
            String upperLevel = level.toUpperCase();
            if (!upperLevel.matches("N[1-5]")) {
                response.put("success", false);
                response.put("message", "Level không hợp lệ. Chỉ hỗ trợ N1-N5");
                return ResponseEntity.badRequest().body(response);
            }

            Page<JlptVocabularyDTO> result;

            if (q != null && !q.trim().isEmpty()) {
                result = jlptVocabularyService.searchByLevel(upperLevel, q.trim(), page, size);
            } else {
                result = jlptVocabularyService.getByLevel(upperLevel, page, size);
            }

            response.put("success", true);
            response.put("data", result.getContent());
            response.put("pagination", Map.of(
                    "page", page,
                    "size", size,
                    "total", result.getTotalElements(),
                    "totalPages", result.getTotalPages()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi lấy dữ liệu " + level.toUpperCase() + ": " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{level}/count")
    public ResponseEntity<Map<String, Object>> getCountByLevel(@PathVariable("level") String level) {
        Map<String, Object> response = new HashMap<>();
        try {
            // SỬA 2: Tương tự cho count endpoint
            String upperLevel = level.toUpperCase();
            if (!upperLevel.matches("N[1-5]")) {
                response.put("success", false);
                response.put("message", "Level không hợp lệ. Chỉ hỗ trợ N1-N5");
                return ResponseEntity.badRequest().body(response);
            }

            long count = jlptVocabularyService.getCountByLevel(upperLevel);
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi đếm từ " + level.toUpperCase() + ": " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getTotalCount() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = jlptVocabularyService.getTotalCount();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi đếm từ vựng: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}