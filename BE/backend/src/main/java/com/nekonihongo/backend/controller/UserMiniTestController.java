// src/main/java/com/nekonihongo/backend/controller/UserMiniTestController.java (UPDATED – CHỈ CÒN ENDPOINT USER)

package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.MiniTestSubmissionDTO;
import com.nekonihongo.backend.service.MiniTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/mini-test")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UserMiniTestController {

    private final MiniTestService miniTestService;

    /**
     * GET /api/user/mini-test/submissions
     * User: Lấy danh sách bài nộp của chính mình
     */
    @GetMapping("/submissions")
    public ResponseEntity<?> getUserSubmissions() {
        try {
            List<MiniTestSubmissionDTO> submissions = miniTestService.getUserSubmissions();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", submissions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy danh sách bài nộp: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/user/mini-test/submission/{id}
     * User: Xóa bài nộp của chính mình
     */
    @DeleteMapping("/submission/{id}")
    public ResponseEntity<?> deleteUserSubmission(@PathVariable("id") Long id) {
        try {
            var response = miniTestService.deleteUserSubmission(id);

            if (response.isSuccess()) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", response.getMessage()));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", response.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi xóa bài nộp: " + e.getMessage()));
        }
    }

    /**
     * GET /api/user/mini-test/feedback-count
     * User: Đếm số bài đã feedback (cho bell badge)
     */
    @GetMapping("/feedback-count")
    public ResponseEntity<?> getFeedbackCount() {
        try {
            int count = miniTestService.getUserFeedbackCount();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy số feedback: " + e.getMessage()));
        }
    }
}