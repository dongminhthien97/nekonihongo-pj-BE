// src/main/java/com/nekonihongo/backend/controller/AdminMiniTestController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.MiniTestSubmissionDTO;
import com.nekonihongo.backend.entity.MiniTestSubmission;
import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.GrammarQuestionRepository;
import com.nekonihongo.backend.service.MiniTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/admin/mini-test", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminMiniTestController {

    private final MiniTestService miniTestService;
    private final GrammarQuestionRepository grammarQuestionRepository;

    private Map<String, Object> createErrorResponse(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Lỗi server: " +
                (e.getMessage() != null ? e.getMessage() : "Không xác định"));
        return errorResponse;
    }

    private Map<String, Object> createSuccessResponse(String key, Object value) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put(key, value);
        return response;
    }

    @GetMapping("")
    public ResponseEntity<?> getSubmissions(
            @RequestParam(name = "filter", required = false, defaultValue = "all") String filter) {

        try {
            List<MiniTestSubmissionDTO> submissions;

            if ("pending".equalsIgnoreCase(filter)) {
                submissions = miniTestService.getPendingSubmissions();
            } else {
                submissions = miniTestService.getAllSubmissions();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", submissions);
            response.put("total", submissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/pending-count")
    public ResponseEntity<?> getPendingCount() {
        try {
            long count = miniTestService.getPendingCount();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingSubmissions() {
        try {
            List<MiniTestSubmissionDTO> submissions = miniTestService.getPendingSubmissions();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", submissions);
            response.put("count", submissions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/submissions")
    public ResponseEntity<?> getAllSubmissions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "submittedAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction) {

        try {
            Sort sort = direction.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            List<MiniTestSubmissionDTO> submissions = miniTestService.getAllSubmissions();

            int start = Math.min(page * size, submissions.size());
            int end = Math.min((page + 1) * size, submissions.size());
            List<MiniTestSubmissionDTO> pagedSubmissions = submissions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pagedSubmissions);
            response.put("currentPage", page);
            response.put("totalItems", submissions.size());
            response.put("totalPages", (int) Math.ceil((double) submissions.size() / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/submission/{id}")
    public ResponseEntity<?> getSubmissionById(@PathVariable(name = "id") Long id) {
        try {
            var submissionOpt = miniTestService.getSubmissionById(id);
            if (submissionOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy bài nộp");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            var submission = submissionOpt.get();
            Map<String, Object> submissionData = new HashMap<>();
            submissionData.put("id", submission.getId());
            submissionData.put("userId", submission.getUserId());
            submissionData.put("lessonId", submission.getLessonId());
            submissionData.put("submittedAt", submission.getSubmittedAt());
            submissionData.put("feedback", submission.getFeedback());
            submissionData.put("feedbackAt", submission.getFeedbackAt());
            submissionData.put("status", submission.getStatus().name());
            submissionData.put("timeSpent", submission.getTimeSpent());
            submissionData.put("score", submission.getScore());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", submissionData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/{submissionId}/details")
    public ResponseEntity<?> getSubmissionDetails(@PathVariable(name = "submissionId") Long submissionId) {
        try {
            Optional<MiniTestSubmission> submissionOpt = miniTestService.getSubmissionById(submissionId);
            if (submissionOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy bài nộp");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            MiniTestSubmission submission = submissionOpt.get();
            Optional<User> userOpt = miniTestService.getUserInfoForSubmission(submissionId);

            List<MiniTestSubmissionDTO.AnswerDTO> answers = miniTestService
                    .parseAnswersToDtoList(submission.getAnswers());

            Map<String, Object> submissionData = new HashMap<>();
            submissionData.put("id", submission.getId());
            submissionData.put("userId", submission.getUserId());
            submissionData.put("lessonId", submission.getLessonId());
            submissionData.put("score", submission.getScore());
            submissionData.put("status", submission.getStatus().name());
            submissionData.put("feedback", submission.getFeedback());
            submissionData.put("feedbackAt", submission.getFeedbackAt());
            submissionData.put("submittedAt", submission.getSubmittedAt());
            submissionData.put("timeSpent", submission.getTimeSpent());
            submissionData.put("answers", answers);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                submissionData.put("userName", user.getFullName() != null ? user.getFullName() : user.getUsername());
                submissionData.put("userEmail", user.getEmail());
            } else {
                submissionData.put("userName", "User " + submission.getUserId());
                submissionData.put("userEmail", "N/A");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", submissionData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @PostMapping("/submission/{id}/feedback")
    public ResponseEntity<?> addFeedbackWithScore(
            @PathVariable(name = "id") Long submissionId,
            @RequestBody Map<String, Object> feedbackRequest) {

        try {
            String feedback = (String) feedbackRequest.get("feedback");
            Integer score = null;

            if (feedbackRequest.containsKey("score")) {
                Object scoreObj = feedbackRequest.get("score");
                if (scoreObj != null) {
                    if (scoreObj instanceof Integer) {
                        score = (Integer) scoreObj;
                    } else if (scoreObj instanceof Double) {
                        score = ((Double) scoreObj).intValue();
                    } else if (scoreObj instanceof String) {
                        String scoreStr = ((String) scoreObj).trim();
                        if (!scoreStr.isEmpty()) {
                            try {
                                score = Integer.parseInt(scoreStr);
                            } catch (NumberFormatException e) {
                                Map<String, Object> errorResponse = new HashMap<>();
                                errorResponse.put("success", false);
                                errorResponse.put("message", "Định dạng điểm không hợp lệ. Phải là số nguyên.");
                                return ResponseEntity.badRequest().body(errorResponse);
                            }
                        } else {
                            score = 0;
                        }
                    } else if (scoreObj instanceof Number) {
                        score = ((Number) scoreObj).intValue();
                    }
                } else {
                    score = 0;
                }
            } else {
                score = 0;
            }

            if (feedback == null || feedback.trim().isEmpty()) {
                feedback = "Đã xem";
            } else {
                feedback = feedback.trim();
            }

            if (score == null) {
                score = 0;
            }

            var result = miniTestService.scoreAndFeedback(submissionId, feedback, score);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());

            if (result.getSubmissionId() != null) {
                response.put("submissionId", result.getSubmissionId());
            }

            if (result.getTestId() != null) {
                response.put("testId", result.getTestId());
            }

            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi server: " +
                    (e.getMessage() != null ? e.getMessage() : "Không xác định"));

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/submission/{id}/manual-score")
    public ResponseEntity<?> submitManualScore(
            @PathVariable(name = "id") Long submissionId,
            @RequestBody Map<String, Object> scoreRequest) {

        try {
            String feedback = (String) scoreRequest.get("feedback");
            Integer totalScore = null;

            if (scoreRequest.containsKey("score")) {
                Object scoreObj = scoreRequest.get("score");
                if (scoreObj != null) {
                    if (scoreObj instanceof Integer) {
                        totalScore = (Integer) scoreObj;
                    } else if (scoreObj instanceof Double) {
                        totalScore = ((Double) scoreObj).intValue();
                    } else if (scoreObj instanceof String) {
                        String scoreStr = ((String) scoreObj).trim();
                        if (!scoreStr.isEmpty()) {
                            try {
                                totalScore = Integer.parseInt(scoreStr);
                            } catch (NumberFormatException e) {
                                Map<String, Object> errorResponse = new HashMap<>();
                                errorResponse.put("success", false);
                                errorResponse.put("message", "Định dạng điểm không hợp lệ. Phải là số nguyên.");
                                return ResponseEntity.badRequest().body(errorResponse);
                            }
                        } else {
                            totalScore = 0;
                        }
                    } else if (scoreObj instanceof Number) {
                        totalScore = ((Number) scoreObj).intValue();
                    }
                } else {
                    totalScore = 0;
                }
            } else {
                totalScore = 0;
            }

            if (feedback == null || feedback.trim().isEmpty()) {
                feedback = "Đã chấm điểm thủ công";
            } else {
                feedback = feedback.trim();
            }

            if (totalScore == null || totalScore < 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Điểm số không hợp lệ");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            var result = miniTestService.scoreAndFeedback(submissionId, feedback, totalScore);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("submissionId", result.getSubmissionId());
            response.put("score", totalScore);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/lesson/{lessonId}/stats")
    public ResponseEntity<?> getLessonStats(@PathVariable(name = "lessonId") Integer lessonId) {
        try {
            long pendingCount = miniTestService.countPendingByLesson(lessonId);
            long feedbackedCount = miniTestService.countFeedbackedByLesson(lessonId);
            List<MiniTestSubmission> submissions = miniTestService.getSubmissionsByLesson(lessonId);

            Map<String, Object> statsData = new HashMap<>();
            statsData.put("lessonId", lessonId);
            statsData.put("totalSubmissions", submissions.size());
            statsData.put("pendingCount", pendingCount);
            statsData.put("feedbackedCount", feedbackedCount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statsData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSubmissions(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "lessonId", required = false) Integer lessonId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        try {
            List<MiniTestSubmissionDTO> allSubmissions = miniTestService.getAllSubmissions();

            List<MiniTestSubmissionDTO> filteredSubmissions = allSubmissions.stream()
                    .filter(submission -> {
                        boolean matches = true;

                        if (keyword != null && !keyword.isEmpty()) {
                            String keywordLower = keyword.toLowerCase();
                            matches = (submission.getUserName() != null
                                    && submission.getUserName().toLowerCase().contains(keywordLower))
                                    || (submission.getUserEmail() != null
                                            && submission.getUserEmail().toLowerCase().contains(keywordLower))
                                    || (submission.getLessonTitle() != null
                                            && submission.getLessonTitle().toLowerCase().contains(keywordLower))
                                    || String.valueOf(submission.getLessonId()).contains(keyword);
                        }

                        if (status != null && !status.isEmpty()) {
                            matches = matches && submission.getStatus().equalsIgnoreCase(status);
                        }

                        if (lessonId != null) {
                            matches = matches && submission.getLessonId().equals(lessonId.longValue());
                        }

                        return matches;
                    })
                    .collect(Collectors.toList());

            int start = Math.min(page * size, filteredSubmissions.size());
            int end = Math.min((page + 1) * size, filteredSubmissions.size());
            List<MiniTestSubmissionDTO> pagedSubmissions = filteredSubmissions.subList(start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pagedSubmissions);
            response.put("currentPage", page);
            response.put("totalItems", filteredSubmissions.size());
            response.put("totalPages", (int) Math.ceil((double) filteredSubmissions.size() / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead() {
        try {
            List<MiniTestSubmissionDTO> pendingSubmissions = miniTestService.getPendingSubmissions();

            int markedCount = 0;
            LocalDateTime now = LocalDateTime.now();

            for (MiniTestSubmissionDTO submission : pendingSubmissions) {
                var result = miniTestService.provideFeedback(submission.getId(), "Đã xem");
                if (result.isSuccess()) {
                    markedCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã đánh dấu " + markedCount + " bài là đã đọc");
            response.put("markedCount", markedCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @DeleteMapping("/submission/{id}")
    public ResponseEntity<?> deleteSubmission(@PathVariable(name = "id") Long id) {
        try {
            var result = miniTestService.deleteSubmissionByAdmin(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());

            if (result.getSubmissionId() != null) {
                response.put("submissionId", result.getSubmissionId());
            }

            if (result.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi server: " +
                    (e.getMessage() != null ? e.getMessage() : "Không xác định"));

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/batch-delete")
    public ResponseEntity<?> batchDeleteSubmissions(@RequestBody Map<String, Object> request) {
        try {
            List<Integer> ids = (List<Integer>) request.get("ids");

            if (ids == null || ids.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Danh sách ID không được để trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<Long> successIds = new ArrayList<>();
            List<Long> failedIds = new ArrayList<>();
            Map<Long, String> errorMessages = new HashMap<>();

            for (Integer id : ids) {
                try {
                    Long submissionId = id.longValue();
                    var result = miniTestService.deleteSubmissionByAdmin(submissionId);

                    if (result.isSuccess()) {
                        successIds.add(submissionId);
                    } else {
                        failedIds.add(submissionId);
                        errorMessages.put(submissionId, result.getMessage());
                    }
                } catch (Exception e) {
                    Long submissionId = id.longValue();
                    failedIds.add(submissionId);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    errorMessages.put(submissionId, errorMsg);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Đã xử lý %d bài nộp", ids.size()));
            response.put("total", ids.size());
            response.put("successCount", successIds.size());
            response.put("failedCount", failedIds.size());

            if (!successIds.isEmpty()) {
                response.put("successIds", successIds);
            }

            if (!failedIds.isEmpty()) {
                response.put("failedIds", failedIds);
                response.put("errors", errorMessages);
            }

            return ResponseEntity.ok(response);

        } catch (ClassCastException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Định dạng request không hợp lệ. IDs phải là mảng số nguyên");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<?> batchDeleteByQueryParam(@RequestParam(name = "ids") List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Danh sách ID không được để trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            List<Long> successIds = new ArrayList<>();
            List<Long> failedIds = new ArrayList<>();
            Map<Long, String> errorMessages = new HashMap<>();

            for (Long submissionId : ids) {
                try {
                    var result = miniTestService.deleteSubmissionByAdmin(submissionId);

                    if (result.isSuccess()) {
                        successIds.add(submissionId);
                    } else {
                        failedIds.add(submissionId);
                        errorMessages.put(submissionId, result.getMessage());
                    }
                } catch (Exception e) {
                    failedIds.add(submissionId);
                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                    errorMessages.put(submissionId, errorMsg);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Đã xử lý %d bài nộp", ids.size()));
            response.put("total", ids.size());
            response.put("successCount", successIds.size());
            response.put("failedCount", failedIds.size());

            if (!successIds.isEmpty()) {
                response.put("successIds", successIds);
            }

            if (!failedIds.isEmpty()) {
                response.put("failedIds", failedIds);
                response.put("errors", errorMessages);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(createErrorResponse(e));
        }
    }

    @GetMapping("/mini-test/max-score/{lessonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMaxScoreForLesson(@PathVariable(name = "lessonId") Integer lessonId) {
        try {
            Integer maxScore = grammarQuestionRepository.sumPointsByLessonId(lessonId);
            Long questionCount = grammarQuestionRepository.countByLessonId(lessonId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("lessonId", lessonId);
            response.put("maxScore", maxScore != null ? maxScore : 0);
            response.put("questionCount", questionCount);
            response.put("averagePointsPerQuestion",
                    questionCount > 0 && maxScore != null ? Math.round((double) maxScore / questionCount) : 10);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error getting max score: " +
                    (e.getMessage() != null ? e.getMessage() : "Không xác định"));

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}