// src/main/java/com/nekonihongo/backend/repository/GrammarPatternRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.GrammarPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarPatternRepository extends JpaRepository<GrammarPattern, Long> {

    // Lấy tất cả ngữ pháp theo level
    List<GrammarPattern> findByLevelOrderByIdAsc(String level);

    // Đếm số lượng ngữ pháp theo level
    Long countByLevel(String level);

    // Lấy tất cả ngữ pháp sắp xếp theo level và id
    List<GrammarPattern> findAllByOrderByLevelAscIdAsc();

    // Lấy số lượng ngữ pháp của tất cả các level
    @Query("SELECT g.level as level, COUNT(g) as count FROM GrammarPattern g GROUP BY g.level ORDER BY g.level")
    List<Object[]> countAllByLevelGroup();
}