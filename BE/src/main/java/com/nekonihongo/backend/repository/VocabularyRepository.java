// src/main/java/com/nekonihongo/backend/repository/VocabularyRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    List<Vocabulary> findByLessonIdOrderByIdAsc(Integer lessonId);

    @Query("SELECT DISTINCT v.lessonId FROM Vocabulary v ORDER BY v.lessonId")
    List<Integer> findAllLessonIds();

    // Tìm kiếm từ vựng
    List<Vocabulary> findByJapaneseContainingIgnoreCaseOrKanjiContainingIgnoreCaseOrVietnameseContainingIgnoreCase(
            String japanese, String kanji, String vietnamese);
}