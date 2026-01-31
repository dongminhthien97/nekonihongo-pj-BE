// GrammarLessonRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.GrammarExample;
import com.nekonihongo.backend.entity.GrammarLesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrammarLessonRepository extends JpaRepository<GrammarLesson, Integer> {

    // Query 1: Lấy tất cả lesson + points (không fetch examples)
    @Query("SELECT l FROM GrammarLesson l LEFT JOIN FETCH l.points ORDER BY l.id")
    List<GrammarLesson> findAllWithPoints();

    // Query 2: Lấy examples cho một list point ids (gọi khi cần)
    @Query("SELECT ge FROM GrammarExample ge WHERE ge.point.id IN :pointIds ORDER BY ge.displayOrder")
    List<GrammarExample> findExamplesByPointIds(@Param("pointIds") List<Long> pointIds);
}