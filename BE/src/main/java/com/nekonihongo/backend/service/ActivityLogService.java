package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.ActivityLogResponse;
import com.nekonihongo.backend.entity.ActivityLog;
import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.ActivityLogRepository;
import com.nekonihongo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getAllLogs() {
        log.info("Getting all activity logs with user info...");

        try {
            List<ActivityLog> logs = activityLogRepository.findAllByOrderByTimestampDesc();
            log.info("Found {} activity logs in database", logs.size());

            return logs.stream()
                    .map(ActivityLogResponse::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching activity logs: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lấy danh sách activity logs: " + e.getMessage(), e);
        }
    }

    // Log activity với userId
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logActivity(Long userId, String action) {
        log.info("📝 [ActivityLogService] START - User: {}, Action: {}", userId, action);

        try {
            // 1. Tìm user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("❌ User not found: {}", userId);
                        return new RuntimeException("User not found: " + userId);
                    });

            log.info("📝 User found: {} ({})", user.getUsername(), user.getId());

            // 2. Tạo entity
            ActivityLog activityLog = ActivityLog.builder()
                    .user(user)
                    .action(action)
                    .timestamp(LocalDateTime.now())
                    .build();

            // 3. Lưu với flush ngay lập tức
            ActivityLog saved = activityLogRepository.saveAndFlush(activityLog);
            log.info("✅ Saved activity log - ID: {}", saved.getId());

            // 4. Verify (optional)
            Long newCount = activityLogRepository.count();
            log.info("📊 Total logs in DB: {}", newCount);

        } catch (Exception e) {
            log.error("❌ ActivityLogService ERROR: {}", e.getMessage(), e);
            // Với REQUIRES_NEW, exception này không rollback transaction chính
            throw e; // Re-throw để caller biết
        }

        log.info("📝 [ActivityLogService] END");
    }

    // Log activity với username (for backward compatibility)
    @Transactional
    public void logActivity(String username, String action) {
        log.info("📝 [ActivityLogService] START logging activity for username: {}, action: {}", username, action);

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("❌ User not found with username: {}", username);
                        return new RuntimeException("User not found with username: " + username);
                    });

            log.info("📝 Found user: {} (id: {})", user.getUsername(), user.getId());

            // Gọi method với userId
            logActivity(user.getId(), action);

        } catch (Exception e) {
            log.error("❌ FAILED to log activity for username {}: {}", username, e.getMessage(), e);
        }
    }

    // Get logs by user id
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getLogsByUserId(Long userId) {
        log.info("Getting logs for userId: {}", userId);

        List<ActivityLog> logs = activityLogRepository.findByUserIdOrderByTimestampDesc(userId);
        log.info("Found {} logs for userId: {}", logs.size(), userId);

        return logs.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Get logs by username
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getLogsByUsername(String username) {
        log.info("Getting logs for username: {}", username);

        List<ActivityLog> logs = activityLogRepository.findByUserUsernameOrderByTimestampDesc(username);
        log.info("Found {} logs for username: {}", logs.size(), username);

        return logs.stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Test method để debug
    @Transactional
    public ActivityLog testCreateLog(Long userId, String testAction) {
        log.info("🧪 TEST: Creating test log for userId: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Test user not found"));

            ActivityLog testLog = ActivityLog.builder()
                    .user(user)
                    .action("TEST: " + testAction)
                    .timestamp(LocalDateTime.now())
                    .build();

            ActivityLog saved = activityLogRepository.save(testLog);
            activityLogRepository.flush();

            log.info("🧪 TEST: Log created with ID: {}", saved.getId());

            // Verify immediately
            Long count = activityLogRepository.count();
            log.info("🧪 TEST: Total logs in DB: {}", count);

            return saved;
        } catch (Exception e) {
            log.error("🧪 TEST FAILED: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Clean old logs (ví dụ: xóa logs cũ hơn 30 ngày)
    @Transactional
    public int cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<ActivityLog> oldLogs = activityLogRepository.findByTimestampBefore(cutoffDate);

        int count = oldLogs.size();
        log.info("Cleaning up {} old activity logs (older than {} days)", count, daysToKeep);

        if (!oldLogs.isEmpty()) {
            activityLogRepository.deleteAll(oldLogs);
            log.info("✅ Cleaned up {} old activity logs", count);
        }

        return count;
    }

    // Get statistics
    @Transactional(readOnly = true)
    public String getStatistics() {
        long totalLogs = activityLogRepository.count();
        long uniqueUsers = activityLogRepository.countDistinctUsers();

        String stats = String.format("Total logs: %d, Unique users: %d", totalLogs, uniqueUsers);
        log.info("📊 Activity Log Statistics: {}", stats);

        return stats;
    }

    public void deleteLog(Long id) {
        if (!activityLogRepository.existsById(id)) {
            throw new RuntimeException("ActivityLog not found with id: " + id);
        }
        activityLogRepository.deleteById(id);
    }
}