// src/main/java/com/nekonihongo/backend/repository/JlptVocabularyRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.JlptVocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JlptVocabularyRepository extends JpaRepository<JlptVocabulary, Long> {

    // Lấy tất cả từ vựng phân trang (chung cho tất cả level)
    Page<JlptVocabulary> findAll(Pageable pageable);

    // Lấy từ vựng theo level cụ thể (N5, N4, N3, N2, N1)
    Page<JlptVocabulary> findByLevel(String level, Pageable pageable);

    // Tìm kiếm chung toàn bộ (không filter level)
    @Query("SELECT v FROM JlptVocabulary v WHERE " +
            "LOWER(v.tuVung) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.hanTu) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.tiengViet) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<JlptVocabulary> searchAll(@Param("query") String query, Pageable pageable);

    // Tìm kiếm theo level + query
    @Query("SELECT v FROM JlptVocabulary v WHERE v.level = :level AND (" +
            "LOWER(v.tuVung) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.hanTu) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.tiengViet) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<JlptVocabulary> searchByLevel(@Param("level") String level, @Param("query") String query, Pageable pageable);

    // Đếm tổng số từ theo level (hữu ích cho pagination/info)
    long countByLevel(String level);

    // Đếm tổng số từ toàn bộ
    long count();
}