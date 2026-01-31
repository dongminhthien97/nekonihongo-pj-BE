// src/main/java/com/nekonihongo/backend/repository/KanjiJlptRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.KanjiJlpt;
import com.nekonihongo.backend.enums.JlptLevelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KanjiJlptRepository extends JpaRepository<KanjiJlpt, Long> {
    // Lấy theo level và sắp xếp theo stt
    List<KanjiJlpt> findByLevelOrderBySttAsc(JlptLevelType level);

    // Lấy tất cả, sắp xếp theo level rồi stt
    List<KanjiJlpt> findAllByOrderByLevelAscSttAsc();

    long countByLevel(JlptLevelType level);
}