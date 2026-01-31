// src/main/java/com/nekonihongo/backend/controller/VocabularyController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.ApiResponse;
import com.nekonihongo.backend.dto.LessonResponse;
import com.nekonihongo.backend.dto.WordResponse;
import com.nekonihongo.backend.entity.Vocabulary;
import com.nekonihongo.backend.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vocabulary")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VocabularyController {

    private final VocabularyRepository vocabularyRepository;

    // Lấy tất cả bài học (danh sách lesson)
    @GetMapping("/lessons")
    public ApiResponse<List<LessonResponse>> getAllLessons() {
        var lessonIds = vocabularyRepository.findAllLessonIds();

        var lessons = lessonIds.stream().map(id -> {
            var words = vocabularyRepository.findByLessonIdOrderByIdAsc(id)
                    .stream()
                    .map(this::toWordResponse)
                    .toList();

            return new LessonResponse(
                    id,
                    "Bài " + id + " – Từ vựng Minna", // bạn có thể thêm bảng lesson riêng nếu muốn title đẹp hơn
                    getIconForLesson(id),
                    words);
        }).toList();

        return ApiResponse.success(lessons);
    }

    // Lấy 1 bài cụ thể
    @GetMapping("/lessons/{lessonId}")
    public ApiResponse<LessonResponse> getLesson(@PathVariable Integer lessonId) {
        var words = vocabularyRepository.findByLessonIdOrderByIdAsc(lessonId)
                .stream()
                .map(this::toWordResponse)
                .toList();

        var lesson = new LessonResponse(
                lessonId,
                "Bài " + lessonId + " – Từ vựng Minna",
                getIconForLesson(lessonId),
                words);

        return ApiResponse.success(lesson);
    }

    // Tìm kiếm từ vựng
    @GetMapping("/search")
    public ApiResponse<List<WordResponse>> search(@RequestParam String q) {
        var results = vocabularyRepository
                .findByJapaneseContainingIgnoreCaseOrKanjiContainingIgnoreCaseOrVietnameseContainingIgnoreCase(q, q, q)
                .stream()
                .map(this::toWordResponse)
                .limit(20)
                .toList();
        return ApiResponse.success(results);
    }

    private WordResponse toWordResponse(Vocabulary v) {
        return new WordResponse(v.getJapanese(), v.getKanji(), v.getVietnamese(), v.getCategory());
    }

    private String getIconForLesson(int lessonId) {
        return "Cat";
    }
}