package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.CheckTestResponseDTO;
import com.nekonihongo.backend.dto.SubmitTestRequestDTO;
import com.nekonihongo.backend.dto.SubmitTestResponseDTO;
import com.nekonihongo.backend.service.MiniTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar-tests")
@RequiredArgsConstructor
public class MiniTestApiController {

    private final MiniTestService miniTestService;

    /**
     * GET /api/grammar-tests/check?lesson_id=X
     * User: Kiểm tra đã submit bài test cho lesson chưa (dùng current user từ
     * token)
     */
    @GetMapping("/check")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> checkTestStatus(@RequestParam("lesson_id") Integer lessonId) {
        try {
            System.out.println(lessonId);

            // Lấy current userId từ service
            Long userId = miniTestService.getCurrentUserId();

            System.out.println("🔍 [MiniTestApiController] User ID: " + userId);

            CheckTestResponseDTO result = miniTestService.checkUserTestStatus(userId,
                    lessonId);

            System.out.println("✅ [MiniTestApiController] Check result: hasSubmitted=" +
                    result.isHasSubmitted() +
                    ", submissionId=" + result.getSubmissionId());

            // SỬA: Dùng HashMap thay vì Map.of() để tránh null
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasSubmitted", result.isHasSubmitted());

            if (result.getSubmissionId() != null) {
                response.put("submissionId", result.getSubmissionId());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ [MiniTestApiController] Error: " + e.getMessage());
            e.printStackTrace();

            // SỬA: Dùng HashMap cho error response
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi kiểm tra trạng thái: " +
                    (e.getMessage() != null ? e.getMessage() : "Unknown error"));

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/grammar-tests/submit
     * User: Submit bài test (dùng current user từ token)
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitTest(@RequestBody SubmitTestRequestDTO request) {
        try {
            System.out.println("🔍 [MiniTestApiController] Submitting test for lesson: "
                    + request.getLessonId());

            // Set userId từ current user (an toàn, frontend không gửi userId)
            Long userId = miniTestService.getCurrentUserId();
            request.setUserId(userId);

            System.out.println("🔍 [MiniTestApiController] User ID: " + userId);
            System.out.println("🔍 [MiniTestApiController] Answers: " +
                    request.getAnswers());

            SubmitTestResponseDTO result = miniTestService.submitTest(request);

            System.out.println("✅ [MiniTestApiController] Submit result: success=" +
                    result.isSuccess() +
                    ", testId=" + result.getTestId());

            // SỬA: Dùng HashMap
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());

            if (result.getTestId() != null) {
                response.put("testId", result.getTestId());
            }

            if (result.getSubmissionId() != null) {
                response.put("submissionId", result.getSubmissionId());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ [MiniTestApiController] Error: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi nộp bài: " +
                    (e.getMessage() != null ? e.getMessage() : "Unknown error"));

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/grammar-tests/{submissionId}/feedback
     * Admin: Thêm feedback cho bài nộp
     */
    @PostMapping("/{submissionId}/feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> provideFeedback(
            @PathVariable Long submissionId,
            @RequestBody Map<String, String> feedbackRequest) {

        try {
            String feedback = feedbackRequest.get("feedback");
            if (feedback == null || feedback.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Feedback không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            SubmitTestResponseDTO result = miniTestService.provideFeedback(submissionId,
                    feedback.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());

            if (result.getSubmissionId() != null) {
                response.put("submissionId", result.getSubmissionId());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ [MiniTestApiController] Error in feedback: " +
                    e.getMessage());
            e.printStackTrace();

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi server: " +
                    (e.getMessage() != null ? e.getMessage() : "Unknown error"));

            return ResponseEntity.internalServerError().body(response);
        }
    }
}