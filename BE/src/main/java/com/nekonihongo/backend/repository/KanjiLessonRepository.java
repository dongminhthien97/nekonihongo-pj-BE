package com.nekonihongo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nekonihongo.backend.entity.KanjiLesson;

public interface KanjiLessonRepository extends JpaRepository<KanjiLesson, Integer> {
    @Query("SELECT kl FROM KanjiLesson kl LEFT JOIN FETCH kl.kanjiList ORDER BY kl.displayOrder")
    List<KanjiLesson> findAllWithKanji();

    @EntityGraph(attributePaths = { "kanjiList", "kanjiList.compounds", "kanjiList.strokePaths" })
    Optional<KanjiLesson> findById(Integer id);
}
