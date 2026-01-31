package com.nekonihongo.backend.service;

import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.UserRepository;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressService {

    private final UserRepository userRepository;
    private final LevelCalculationService levelService;

    /**
     * Lấy thông tin progress của user
     */
    public UserProgressResponse getUserProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LevelCalculationService.LevelInfo levelInfo = levelService.getLevelInfo(user.getPoints());

        return UserProgressResponse.builder()
                .userId(userId)
                .levelInfo(levelInfo)
                .streak(user.getStreak())
                .longestStreak(user.getLongestStreak())
                .joinDate(user.getJoinDate())
                .lastLoginDate(user.getLastLoginDate())
                .build();
    }

    // DTOs
    @Data
    @Builder
    public static class UserProgressResponse {
        private Long userId;
        private LevelCalculationService.LevelInfo levelInfo;
        private int streak;
        private int longestStreak;
        private int totalExercisesCompleted;
        private java.time.LocalDate joinDate;
        private java.time.LocalDateTime lastLoginDate;
    }
}