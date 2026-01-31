package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.MiniTestSubmission;
import com.nekonihongo.backend.entity.MiniTestSubmission.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MiniTestSubmissionRepository extends JpaRepository<MiniTestSubmission, Long> {

    // ADMIN: Đếm số bài pending (toàn hệ thống)
    long countByStatus(Status status);

    // ADMIN: Lấy danh sách bài pending, sort mới nhất trước
    List<MiniTestSubmission> findByStatusOrderBySubmittedAtDesc(Status status);

    // USER: Lấy submissions của user hiện tại, sort mới nhất trước
    List<MiniTestSubmission> findByUserIdOrderBySubmittedAtDesc(Long userId);

    // USER: Đếm số bài đã feedback của user (cho bell badge)
    long countByUserIdAndStatus(Long userId, Status status);

    // Optional: Lấy submissions của user với status cụ thể (nếu cần filter
    // pending/feedbacked)
    List<MiniTestSubmission> findByUserIdAndStatusOrderBySubmittedAtDesc(Long userId, Status status);

    // =========== THÊM MỚI THEO YÊU CẦU FRONTEND ===========

    // 1. Kiểm tra user đã submit bài cho lesson chưa (CHO API:
    // /grammar-tests/check)
    @Query("SELECT COUNT(s) > 0 FROM MiniTestSubmission s WHERE s.userId = :userId AND s.lessonId = :lessonId")
    boolean existsByUserIdAndLessonId(@Param("userId") Long userId, @Param("lessonId") Integer lessonId);

    // 2. Tìm submission theo user và lesson (để lấy submission nếu đã có)
    Optional<MiniTestSubmission> findByUserIdAndLessonId(Long userId, Integer lessonId);

    // 3. Lấy tất cả submissions của user theo lesson
    List<MiniTestSubmission> findByUserIdAndLessonIdOrderBySubmittedAtDesc(Long userId, Integer lessonId);

    // 4. Tìm submissions theo lesson (cho admin xem tất cả bài nộp của 1 lesson)
    List<MiniTestSubmission> findByLessonId(Integer lessonId);

    // 5. Tìm submissions theo lesson và status
    List<MiniTestSubmission> findByLessonIdAndStatus(Integer lessonId, Status status);

    // 6. Tìm submissions trong khoảng thời gian
    List<MiniTestSubmission> findBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);

    // 8. Lấy số bài pending theo lesson (cho admin dashboard)
    @Query("SELECT COUNT(s) FROM MiniTestSubmission s WHERE s.lessonId = :lessonId AND s.status = 'pending'")
    long countPendingByLessonId(@Param("lessonId") Integer lessonId);

    // Thêm các methods cho admin

    @Query("SELECT COUNT(s) FROM MiniTestSubmission s WHERE s.lessonId = :lessonId AND s.status = 'feedbacked'")
    long countFeedbackedByLessonId(@Param("lessonId") Integer lessonId);

    List<MiniTestSubmission> findAllByOrderBySubmittedAtDesc();
}