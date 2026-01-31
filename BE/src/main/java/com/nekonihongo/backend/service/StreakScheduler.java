// src/main/java/com/nekonihongo/backend/service/StreakScheduler.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StreakScheduler {

    private final UserRepository userRepository;

    /**
     * Tự động kiểm tra và reset streak nếu user không đăng nhập >= 2 ngày
     * Chạy mỗi ngày lúc 00:00
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkAndResetStreaks() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        userRepository.resetStreaksForInactiveUsers(twoDaysAgo);
    }
}