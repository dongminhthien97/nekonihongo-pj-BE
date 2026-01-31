// KatakanaController.java (tương tự)
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.KatakanaDTO;
import com.nekonihongo.backend.dto.request.KatakanaRequest;
import com.nekonihongo.backend.service.KatakanaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/katakana")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KatakanaController {
    private final KatakanaService katakanaService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKatakana() {
        List<KatakanaDTO> katakanaList = katakanaService.getAllKatakana();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", katakanaList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{character}")
    public ResponseEntity<Map<String, Object>> getKatakanaDetail(@PathVariable String character) {
        KatakanaDTO katakana = katakanaService.getByCharacter(character);
        Map<String, Object> response = new HashMap<>();

        if (katakana != null) {
            response.put("success", true);
            response.put("data", katakana);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Katakana not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createKatakana(@Valid @RequestBody KatakanaRequest request) {
        try {
            KatakanaDTO created = katakanaService.createKatakana(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Katakana created successfully");
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
    public ResponseEntity<Map<String, Object>> updateKatakana(
            @PathVariable Integer id,
            @Valid @RequestBody KatakanaRequest request) {
        try {
            KatakanaDTO updated = katakanaService.updateKatakana(id, request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Katakana updated successfully");
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
    public ResponseEntity<Map<String, Object>> deleteKatakana(@PathVariable Integer id) {
        try {
            katakanaService.deleteKatakana(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Katakana deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}