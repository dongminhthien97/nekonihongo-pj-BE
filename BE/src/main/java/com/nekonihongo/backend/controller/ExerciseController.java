package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.ApiResponse;
import com.nekonihongo.backend.dto.ExerciseDTO;
import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.ActivityLogRepository;
import com.nekonihongo.backend.service.ExerciseService;
import com.nekonihongo.backend.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final UserService userService;
    private final ActivityLogRepository activityLogRepository;

    @GetMapping("/vocabulary/n5")
    public ResponseEntity<List<ExerciseDTO>> getN5VocabularyExercises() {
        return ResponseEntity.ok(exerciseService.getN5VocabularyExercises());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExercise(@PathVariable("id") Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @GetMapping("/grammar/n5")
    public ResponseEntity<List<ExerciseDTO>> getN5GrammarExercises() {
        return ResponseEntity.ok(exerciseService.getN5GrammarExercises());
    }

    @GetMapping("/kanji/n5")
    public ResponseEntity<List<ExerciseDTO>> getN5KanjiExercises() {
        return ResponseEntity.ok(exerciseService.getN5KanjiExercises());
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExerciseService.SubmitExerciseResult>> submitExercise(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExerciseService.SubmitExerciseRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Vui lòng đăng nhập để nộp bài tập"));
        }

        String identifier = userDetails.getUsername();
        Optional<User> userOpt = userService.findByUsernameOrEmailIgnoreCase(identifier);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy người dùng với username/email: " + identifier));
        }

        try {
            User currentUser = userOpt.get();
            ExerciseService.SubmitExerciseResult result = exerciseService.submitExercise(currentUser.getId(), request);

            return ResponseEntity.ok(ApiResponse.success("Nộp bài tập thành công!", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi server khi nộp bài tập: " + e.getMessage()));
        }
    }

    @GetMapping("/debug/logs/count")
    public ResponseEntity<ApiResponse<Long>> getLogCount() {
        try {
            long count = activityLogRepository.count();
            return ResponseEntity.ok(ApiResponse.success("Total activity logs", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/debug/logs/latest")
    public ResponseEntity<ApiResponse<?>> getLatestLogs() {
        try {
            List<Object[]> logs = activityLogRepository.findLatestLogs(5);

            List<Object> result = logs.stream()
                    .map(log -> {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("id", log[0]);
                        map.put("userId", log[1]);
                        map.put("username", log[2]);
                        map.put("action", log[3]);
                        map.put("timestamp", log[4]);
                        return map;
                    })
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Latest activity logs", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/debug/test-log")
    public ResponseEntity<ApiResponse<String>> testLogCreation(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Unauthorized"));
            }

            String identifier = userDetails.getUsername();
            Optional<User> userOpt = userService.findByUsernameOrEmailIgnoreCase(identifier);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }

            User user = userOpt.get();
            String testAction = "Test log from debug endpoint at " + LocalDateTime.now();

            com.nekonihongo.backend.entity.ActivityLog testLog = com.nekonihongo.backend.entity.ActivityLog.builder()
                    .user(user)
                    .action(testAction)
                    .timestamp(LocalDateTime.now())
                    .build();

            activityLogRepository.save(testLog);
            activityLogRepository.flush();

            return ResponseEntity.ok(ApiResponse.success(
                    "Test log created successfully with ID: " + testLog.getId(),
                    testAction));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}