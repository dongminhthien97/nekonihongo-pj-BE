package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.QuestionResponseDTO;
import com.nekonihongo.backend.service.GrammarQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
public class GrammarQuestionController {

    private final GrammarQuestionService grammarQuestionService;

    /**
     * GET /api/grammar/mini-test/questions?lesson_id=X
     * Lấy câu hỏi theo lesson
     */
    @GetMapping("/mini-test/questions")
    public ResponseEntity<?> getQuestions(@RequestParam("lesson_id") Integer lessonId) {
        try {
            List<QuestionResponseDTO> questions = grammarQuestionService.getQuestionsByLesson(lessonId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", questions,
                    "count", questions.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage()));
        }
    }
}