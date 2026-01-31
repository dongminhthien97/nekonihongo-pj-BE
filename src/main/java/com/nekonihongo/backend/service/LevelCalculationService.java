package com.nekonihongo.backend.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LevelCalculationService {

    // Cache thresholds để tăng performance
    private static final Map<Integer, Integer> LEVEL_THRESHOLDS = new HashMap<>();

    static {
        LEVEL_THRESHOLDS.put(1, 0); // Level 1: 0 điểm
        LEVEL_THRESHOLDS.put(2, 30); // Level 2: 30 điểm (3 bài)
        LEVEL_THRESHOLDS.put(3, 70); // Level 3: 70 điểm (4 bài thêm)
        LEVEL_THRESHOLDS.put(4, 120); // Level 4: 120 điểm (5 bài thêm)
        LEVEL_THRESHOLDS.put(5, 180); // Level 5: 180 điểm (6 bài thêm)
        LEVEL_THRESHOLDS.put(6, 250); // Level 6: 250 điểm (7 bài thêm)
        LEVEL_THRESHOLDS.put(7, 330); // Level 7: 330 điểm (8 bài thêm)
        LEVEL_THRESHOLDS.put(8, 420); // Level 8: 420 điểm (9 bài thêm)
        LEVEL_THRESHOLDS.put(9, 520); // Level 9: 520 điểm (10 bài thêm)
        LEVEL_THRESHOLDS.put(10, 630);// Level 10: 630 điểm (11 bài thêm)
    }

    /**
     * Tính level dựa trên điểm
     */
    public int calculateLevel(int points) {
        if (points < 30)
            return 1;
        if (points < 70)
            return 2;
        if (points < 120)
            return 3;
        if (points < 180)
            return 4;
        if (points < 250)
            return 5;
        if (points < 330)
            return 6;
        if (points < 420)
            return 7;
        if (points < 520)
            return 8;
        if (points < 630)
            return 9;
        if (points < 750)
            return 10;

        // Công thức cho level > 10: level = 10 + (points - 630) / 150
        return 10 + (points - 630) / 150;
    }

    /**
     * Lấy điểm cần cho level tiếp theo
     */
    public int getNextLevelPoints(int currentLevel) {
        if (currentLevel < 10) {
            return LEVEL_THRESHOLDS.getOrDefault(currentLevel + 1, 0);
        }

        // Công thức cho level > 10: points = 630 + (level - 9) * 150
        return 630 + (currentLevel - 9) * 150;
    }

    /**
     * Lấy điểm bắt đầu của level hiện tại
     */
    public int getCurrentLevelStartPoints(int currentLevel) {
        if (currentLevel <= 10) {
            return LEVEL_THRESHOLDS.getOrDefault(currentLevel, 0);
        }

        // Công thức cho level > 10: points = 630 + (level - 10) * 150
        return 630 + (currentLevel - 10) * 150;
    }

    /**
     * Tính điểm trong level hiện tại
     */
    public int getPointsInCurrentLevel(int totalPoints, int currentLevel) {
        int levelStartPoints = getCurrentLevelStartPoints(currentLevel);
        return totalPoints - levelStartPoints;
    }

    /**
     * Tính điểm cần cho level tiếp theo
     */
    public int getPointsNeededForNextLevel(int totalPoints, int currentLevel) {
        int nextLevelPoints = getNextLevelPoints(currentLevel);
        return Math.max(0, nextLevelPoints - totalPoints);
    }

    /**
     * Tính % tiến độ lên level tiếp theo
     */
    public double getProgressToNextLevel(int totalPoints, int currentLevel) {
        int currentLevelStart = getCurrentLevelStartPoints(currentLevel);
        int nextLevelPoints = getNextLevelPoints(currentLevel);

        if (nextLevelPoints <= currentLevelStart)
            return 100.0;

        double progress = (totalPoints - currentLevelStart) * 100.0 /
                (nextLevelPoints - currentLevelStart);
        return Math.min(progress, 100.0);
    }

    /**
     * Tính số bài tập cần cho level tiếp theo
     */
    public int getExercisesNeededForNextLevel(int pointsNeeded) {
        // Giả sử mỗi bài tối đa 10 điểm
        return (int) Math.ceil(pointsNeeded / 10.0);
    }

    /**
     * Kiểm tra xem user có lên level không
     */
    public LevelUpResult checkLevelUp(int oldPoints, int newPoints) {
        int oldLevel = calculateLevel(oldPoints);
        int newLevel = calculateLevel(newPoints);

        if (newLevel > oldLevel) {
            return LevelUpResult.builder()
                    .leveledUp(true)
                    .oldLevel(oldLevel)
                    .newLevel(newLevel)
                    .levelsGained(newLevel - oldLevel)
                    .build();
        }

        return LevelUpResult.builder()
                .leveledUp(false)
                .oldLevel(oldLevel)
                .newLevel(newLevel)
                .levelsGained(0)
                .build();
    }

    /**
     * Lấy thông tin chi tiết về level
     */
    public LevelInfo getLevelInfo(int totalPoints) {
        int currentLevel = calculateLevel(totalPoints);
        int nextLevelPoints = getNextLevelPoints(currentLevel);
        int pointsInCurrentLevel = getPointsInCurrentLevel(totalPoints, currentLevel);
        int pointsNeeded = getPointsNeededForNextLevel(totalPoints, currentLevel);
        double progress = getProgressToNextLevel(totalPoints, currentLevel);
        int exercisesNeeded = getExercisesNeededForNextLevel(pointsNeeded);

        return LevelInfo.builder()
                .currentLevel(currentLevel)
                .totalPoints(totalPoints)
                .nextLevelPoints(nextLevelPoints)
                .pointsInCurrentLevel(pointsInCurrentLevel)
                .pointsNeededForNextLevel(pointsNeeded)
                .progressToNextLevel(progress)
                .exercisesNeededForNextLevel(exercisesNeeded)
                .build();
    }

    /**
     * Tính điểm kiếm được từ bài tập
     */
    public int calculatePointsEarned(int correctAnswers, int totalQuestions, int difficultyLevel) {
        if (totalQuestions == 0)
            return 0;

        double percentage = (double) correctAnswers / totalQuestions;
        int basePoints = (int) Math.round(percentage * 10); // 0-10 điểm

        // Hệ số độ khó: 1=1x, 2=1.2x, 3=1.5x, 4=2x, 5=3x
        double difficultyMultiplier = switch (difficultyLevel) {
            case 1 -> 1.0;
            case 2 -> 1.2;
            case 3 -> 1.5;
            case 4 -> 2.0;
            case 5 -> 3.0;
            default -> 1.0;
        };

        return (int) Math.round(basePoints * difficultyMultiplier);
    }

    // DTOs
    @Data
    @Builder
    public static class LevelUpResult {
        private boolean leveledUp;
        private int oldLevel;
        private int newLevel;
        private int levelsGained;
    }

    @Data
    @Builder
    public static class LevelInfo {
        private int currentLevel;
        private int totalPoints;
        private int nextLevelPoints;
        private int pointsInCurrentLevel;
        private int pointsNeededForNextLevel;
        private double progressToNextLevel;
        private int exercisesNeededForNextLevel;
    }
}