package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.nekonihongo.backend.entity.ActivityLog;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private Long userId; // Thêm userId
    private String username; // Giữ username để hiển thị
    private String fullName; // Có thể thêm fullName nếu cần
    private String avatarUrl; // Có thể thêm avatar nếu cần
    private String action;
    private LocalDateTime timestamp;

    // Constructor từ entity
    public static ActivityLogResponse fromEntity(ActivityLog activityLog) {
        return ActivityLogResponse.builder()
                .id(activityLog.getId())
                .userId(activityLog.getUser().getId())
                .username(activityLog.getUser().getUsername())
                .fullName(activityLog.getUser().getFullName())
                .avatarUrl(activityLog.getUser().getAvatarUrl())
                .action(activityLog.getAction())
                .timestamp(activityLog.getTimestamp())
                .build();
    }
}