// src/main/java/com/nekonihongo/backend/controller/NotificationController.java
package com.nekonihongo.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNotification(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("user_id")).longValue();
            String type = (String) request.get("type");
            String title = (String) request.get("title");
            String message = (String) request.get("message");
            Long relatedId = request.get("related_id") != null ? ((Number) request.get("related_id")).longValue()
                    : null;

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification created successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error creating notification: " + e.getMessage()));
        }
    }
}