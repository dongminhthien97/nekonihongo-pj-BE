// GrammarController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.ApiResponse;
import com.nekonihongo.backend.dto.GrammarPatternDTO;
import com.nekonihongo.backend.dto.grammar.GrammarExampleDto;
import com.nekonihongo.backend.dto.grammar.GrammarLessonDto;
import com.nekonihongo.backend.dto.grammar.GrammarPointDto;
import com.nekonihongo.backend.entity.GrammarExample;
import com.nekonihongo.backend.entity.GrammarLesson;
import com.nekonihongo.backend.entity.GrammarPoint;
import com.nekonihongo.backend.repository.GrammarLessonRepository;
import com.nekonihongo.backend.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class GrammarController {

        private final GrammarLessonRepository lessonRepository;
        private final GrammarService grammarService;

        @GetMapping("/lessons")
        public ApiResponse<List<GrammarLessonDto>> getAllLessons() {
                // Bước 1: Lấy tất cả lesson + points
                List<GrammarLesson> lessons = lessonRepository.findAllWithPoints();

                // Bước 2: Thu thập pointIds
                List<Long> pointIds = lessons.stream()
                                .flatMap(lesson -> lesson.getPoints().stream())
                                .map(GrammarPoint::getId)
                                .distinct()
                                .toList();

                // Bước 3: Lấy examples một lần duy nhất
                final Map<Long, List<GrammarExample>> examplesMap;
                if (pointIds.isEmpty()) {
                        examplesMap = Map.of();
                } else {
                        List<GrammarExample> allExamples = lessonRepository.findExamplesByPointIds(pointIds);
                        examplesMap = allExamples.stream()
                                        .collect(Collectors.groupingBy(ex -> ex.getPoint().getId()));
                }

                // Bước 4: Gán examples vào points (examplesMap là final → không lỗi lambda)
                lessons.forEach(lesson -> lesson.getPoints().forEach(
                                point -> point.setExamples(examplesMap.getOrDefault(point.getId(), List.of()))));

                // Bước 5: Convert sang DTO
                List<GrammarLessonDto> dtos = lessons.stream()
                                .map(lesson -> new GrammarLessonDto(
                                                lesson.getId(),
                                                lesson.getTitle(),
                                                lesson.getIcon(),
                                                lesson.getPoints().stream()
                                                                .map(point -> new GrammarPointDto(
                                                                                point.getTitle(),
                                                                                point.getMeaning(),
                                                                                point.getExplanation(),
                                                                                point.getExamples().stream()
                                                                                                .map(ex -> new GrammarExampleDto(
                                                                                                                ex.getJapanese(),
                                                                                                                ex.getVietnamese()))
                                                                                                .toList()))
                                                                .toList()))
                                .toList();

                return ApiResponse.success("Lấy danh sách bài học ngữ pháp thành công!", dtos);
        }

        @GetMapping("/lessons/{id}")
        public ApiResponse<GrammarLessonDto> getLesson(@PathVariable Integer id) {
                GrammarLesson lesson = lessonRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Không tìm thấy bài học ngữ pháp với id: " + id));

                // Lấy pointIds của lesson này
                List<Long> pointIds = lesson.getPoints().stream()
                                .map(GrammarPoint::getId)
                                .toList();

                // Tạo examplesMap không gán lại → effectively final
                final Map<Long, List<GrammarExample>> examplesMap;
                if (pointIds.isEmpty()) {
                        examplesMap = Map.of();
                } else {
                        List<GrammarExample> examples = lessonRepository.findExamplesByPointIds(pointIds);
                        examplesMap = examples.stream()
                                        .collect(Collectors.groupingBy(ex -> ex.getPoint().getId()));
                }

                // Gán examples vào points
                lesson.getPoints().forEach(
                                point -> point.setExamples(examplesMap.getOrDefault(point.getId(), List.of())));

                // Convert to DTO
                GrammarLessonDto dto = new GrammarLessonDto(
                                lesson.getId(),
                                lesson.getTitle(),
                                lesson.getIcon(),
                                lesson.getPoints().stream()
                                                .map(point -> new GrammarPointDto(
                                                                point.getTitle(),
                                                                point.getMeaning(),
                                                                point.getExplanation(),
                                                                point.getExamples().stream()
                                                                                .map(ex -> new GrammarExampleDto(
                                                                                                ex.getJapanese(),
                                                                                                ex.getVietnamese()))
                                                                                .toList()))
                                                .toList());

                return ApiResponse.success("Lấy bài học ngữ pháp thành công!", dto);
        }

        // API chung cho JLPT (tất cả các level)
        @GetMapping("/jlpt/{level}")
        public ApiResponse<List<GrammarPatternDTO>> getJlptGrammarByLevel(@PathVariable("level") String level) {
                List<GrammarPatternDTO> patterns = grammarService.getGrammarPatternsByLevel(level.toUpperCase());
                return ApiResponse.success("Lấy danh sách ngữ pháp JLPT " + level + " thành công!", patterns);
        }

        @GetMapping("/jlpt/{level}/count")
        public ApiResponse<Long> getJlptGrammarCount(@PathVariable("level") String level) {
                Long count = grammarService.getGrammarCountByLevel(level.toUpperCase());
                return ApiResponse.success("Lấy số lượng ngữ pháp JLPT " + level + " thành công!", count);
        }

        // API lấy tất cả ngữ pháp JLPT (tất cả level)
        @GetMapping("/jlpt/all")
        public ApiResponse<List<GrammarPatternDTO>> getAllJlptGrammar() {
                List<GrammarPatternDTO> patterns = grammarService.getAllGrammarPatterns();
                return ApiResponse.success("Lấy tất cả ngữ pháp JLPT thành công!", patterns);
        }

        // API lấy số lượng ngữ pháp của tất cả các level
        @GetMapping("/jlpt/counts/all")
        public ApiResponse<Map<String, Long>> getGrammarCountsAllLevels() {
                Map<String, Long> counts = grammarService.getGrammarCountsByAllLevels();
                return ApiResponse.success("Lấy số lượng ngữ pháp tất cả level thành công!", counts);
        }

        // API giữ lại cho tương thích (redirect đến API mới)
        @GetMapping("/n5")
        public ApiResponse<List<GrammarPatternDTO>> getN5Grammar() {
                List<GrammarPatternDTO> patterns = grammarService.getGrammarPatternsByLevel("N5");
                return ApiResponse.success("Lấy danh sách ngữ pháp N5 thành công!", patterns);
        }
}