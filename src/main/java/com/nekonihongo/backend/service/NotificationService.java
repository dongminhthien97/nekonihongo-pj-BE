package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.NotificationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void createNotification(NotificationRequestDTO request) {
        // Lưu notification vào database hoặc gửi real-time notification
        log.info("Notification created: {} - {} - {}",
                request.getType(), request.getTitle(), request.getMessage());

        // TODO: Implement actual notification saving logic
        // Ví dụ: notificationRepository.save(notification);
    }
}