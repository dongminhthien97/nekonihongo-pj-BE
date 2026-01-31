package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Tìm theo user id
    List<ActivityLog> findByUserIdOrderByTimestampDesc(Long userId);

    // Tìm theo username (through user relationship)
    List<ActivityLog> findByUserUsernameOrderByTimestampDesc(String username);

    // Tìm tất cả, sắp xếp mới nhất trước
    List<ActivityLog> findAllByOrderByTimestampDesc();

    // Tìm theo khoảng thời gian
    List<ActivityLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Tìm logs cũ hơn một thời điểm
    List<ActivityLog> findByTimestampBefore(LocalDateTime before);

    // Đếm số lượng user duy nhất
    @Query("SELECT COUNT(DISTINCT al.user.id) FROM ActivityLog al")
    long countDistinctUsers();

    // Lấy logs mới nhất với giới hạn
    List<ActivityLog> findTop10ByOrderByTimestampDesc();

    // Query để lấy log mới nhất với user info
    @Query("SELECT al.id, u.username, al.action, al.timestamp FROM ActivityLog al " +
            "JOIN al.user u ORDER BY al.timestamp DESC")
    List<Object[]> findLatestLogs(@Param("limit") int limit);

    // Query native để debug
    @Query(value = "SELECT al.id, u.username, al.action, al.timestamp " +
            "FROM activity_logs al " +
            "JOIN users u ON al.user_id = u.id " +
            "ORDER BY al.timestamp DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findLatestLogWithUser();

    // Đếm logs của một user cụ thể
    @Query("SELECT COUNT(al) FROM ActivityLog al WHERE al.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}