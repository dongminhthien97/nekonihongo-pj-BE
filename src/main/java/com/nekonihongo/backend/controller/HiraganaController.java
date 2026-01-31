// HiraganaController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.HiraganaDTO;
import com.nekonihongo.backend.dto.request.HiraganaRequest;
import com.nekonihongo.backend.service.HiraganaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hiragana")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HiraganaController {
    private final HiraganaService hiraganaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHiragana() {
        List<HiraganaDTO> hiraganaList = hiraganaService.getAllHiragana();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", hiraganaList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{character}")
    public ResponseEntity<Map<String, Object>> getHiraganaDetail(@PathVariable String character) {
        HiraganaDTO hiragana = hiraganaService.getByCharacter(character);
        Map<String, Object> response = new HashMap<>();

        if (hiragana != null) {
            response.put("success", true);
            response.put("data", hiragana);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Hiragana not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createHiragana(@Valid @RequestBody HiraganaRequest request) {
        try {
            HiraganaDTO created = hiraganaService.createHiragana(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hiragana created successfully");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHiragana(
            @PathVariable Integer id,
            @Valid @RequestBody HiraganaRequest request) {
        try {
            HiraganaDTO updated = hiraganaService.updateHiragana(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hiragana updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHiragana(@PathVariable Integer id) {
        try {
            hiraganaService.deleteHiragana(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hiragana deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}