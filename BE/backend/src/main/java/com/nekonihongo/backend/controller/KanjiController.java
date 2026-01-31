// src/main/java/com/nekonihongo/backend/controller/KanjiController.java
package com.nekonihongo.backend.controller;

import com.nekonihongo.backend.dto.ApiResponse;
import com.nekonihongo.backend.dto.KanjiJlptDTO;
import com.nekonihongo.backend.dto.kanji.KanjiCompoundDto;
import com.nekonihongo.backend.dto.kanji.KanjiDto;
import com.nekonihongo.backend.dto.kanji.KanjiLessonDto;
import com.nekonihongo.backend.entity.KanjiLesson;
import com.nekonihongo.backend.enums.JlptLevelType;
import com.nekonihongo.backend.repository.KanjiLessonRepository;
import com.nekonihongo.backend.service.KanjiService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/kanji")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class KanjiController {

        private final KanjiLessonRepository repository;
        private final KanjiService kanjiService;

        @GetMapping("/lessons")
        public ApiResponse<List<KanjiLessonDto>> getAllLessons() {
                List<KanjiLesson> lessons = repository.findAllWithKanji();

                List<KanjiLessonDto> dtos = lessons.stream()
                                .sorted(Comparator.comparing(KanjiLesson::getDisplayOrder))
                                .map(lesson -> new KanjiLessonDto(
                                                lesson.getId(),
                                                lesson.getTitle(),
                                                lesson.getIcon(),
                                                lesson.getKanjiList().stream()
                                                                .sorted(Comparator.comparing(k -> k.getDisplayOrder()))
                                                                .map(k -> new KanjiDto(
                                                                                k.getKanji(),
                                                                                k.getOnReading(),
                                                                                k.getKunReading() != null
                                                                                                ? k.getKunReading()
                                                                                                : "",
                                                                                k.getHanViet(),
                                                                                k.getMeaning(),
                                                                                k.getStrokes(),
                                                                                k.getCompounds().stream()
                                                                                                .sorted(Comparator
                                                                                                                .comparing(c -> c
                                                                                                                                .getDisplayOrder()))
                                                                                                .map(c -> new KanjiCompoundDto(
                                                                                                                c.getWord(),
                                                                                                                c.getReading(),
                                                                                                                c.getMeaning()))
                                                                                                .toList()))
                                                                .toList()))
                                .toList();

                return ApiResponse.success("Lấy danh sách bài học Kanji thành công!", dtos);
        }

        @GetMapping("/lessons/{id}")
        public ApiResponse<KanjiLessonDto> getLesson(@PathVariable Integer id) {
                KanjiLesson lesson = repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học Kanji"));

                KanjiLessonDto dto = new KanjiLessonDto(
                                lesson.getId(),
                                lesson.getTitle(),
                                lesson.getIcon(),
                                lesson.getKanjiList().stream()
                                                .sorted(Comparator.comparing(k -> k.getDisplayOrder()))
                                                .map(k -> new KanjiDto(
                                                                k.getKanji(),
                                                                k.getOnReading(),
                                                                k.getKunReading() != null ? k.getKunReading() : "",
                                                                k.getHanViet(),
                                                                k.getMeaning(),
                                                                k.getStrokes(),
                                                                k.getCompounds().stream()
                                                                                .sorted(Comparator.comparing(c -> c
                                                                                                .getDisplayOrder()))
                                                                                .map(c -> new KanjiCompoundDto(
                                                                                                c.getWord(),
                                                                                                c.getReading(),
                                                                                                c.getMeaning()))
                                                                                .toList()))
                                                .toList());

                return ApiResponse.success("Lấy bài học Kanji thành công!", dto);
        }

        // API mới - lấy kanji theo cấp độ JLPT
        @GetMapping("/jlpt/{level}")
        public ApiResponse<List<KanjiJlptDTO>> getKanjiByJlptLevel(@PathVariable("level") String level) {
                try {
                        JlptLevelType jlptLevel = JlptLevelType.valueOf(level.toUpperCase());
                        List<KanjiJlptDTO> kanjiList = kanjiService.getKanjiByLevel(jlptLevel);
                        return ApiResponse.success(
                                        String.format("Lấy danh sách Kanji %s thành công!", level.toUpperCase()),
                                        kanjiList);
                } catch (IllegalArgumentException e) {
                        return ApiResponse.error("Cấp độ JLPT không hợp lệ. Các cấp độ: N5, N4, N3, N2, N1");
                }
        }

        // API lấy tất cả kanji JLPT
        @GetMapping("/jlpt/all")
        public ApiResponse<List<KanjiJlptDTO>> getAllJlptKanji() {
                List<KanjiJlptDTO> kanjiList = kanjiService.getAllJlptKanji();
                return ApiResponse.success("Lấy tất cả Kanji JLPT thành công!", kanjiList);
        }

        @GetMapping("/jlpt/{level}/count")
        public ApiResponse<Long> getKanjiCountByJlptLevel(@PathVariable("level") String level) {
                try {
                        JlptLevelType jlptLevel = JlptLevelType.valueOf(level.toUpperCase());
                        long count = kanjiService.getKanjiCountByLevel(jlptLevel);
                        return ApiResponse.success(
                                        String.format("Lấy số lượng Kanji %s thành công!", level.toUpperCase()),
                                        count);
                } catch (IllegalArgumentException e) {
                        return ApiResponse.error("Cấp độ JLPT không hợp lệ. Các cấp độ: N5, N4, N3, N2, N1");
                }
        }
}