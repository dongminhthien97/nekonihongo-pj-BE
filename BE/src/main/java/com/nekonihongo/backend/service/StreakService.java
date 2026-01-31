package com.nekonihongo.backend.service;

import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserRepository userRepository;

    /**
     * Cập nhật streak khi user đăng nhập.
     * - Lần đầu đăng nhập: streak = 1
     * - Đăng nhập liên tiếp: streak +1
     * - Bỏ lỡ >= 2 ngày: streak reset = 1
     * - Đã đăng nhập hôm nay: giữ nguyên
     */
    @Transactional
    public void updateLoginStreak(User user) {
        LocalDate today = LocalDate.now();

        if (user.getLastLoginDate() == null) {
            user.setStreak(1);
            user.setLongestStreak(1);
        } else {
            LocalDate lastLoginDate = user.getLastLoginDate().toLocalDate();

            if (lastLoginDate.equals(today)) {
                return;
            } else if (lastLoginDate.equals(today.minusDays(1))) {
                user.setStreak(user.getStreak() + 1);

                if (user.getStreak() > user.getLongestStreak()) {
                    user.setLongestStreak(user.getStreak());
                }
            } else {
                user.setStreak(1);
            }
        }

        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Kiểm tra streak còn hoạt động (đăng nhập hôm nay hoặc hôm qua).
     */
    public boolean isStreakActive(User user) {
        if (user.getLastLoginDate() == null) {
            return false;
        }

        LocalDate lastLogin = user.getLastLoginDate().toLocalDate();
        LocalDate today = LocalDate.now();

        return lastLogin.equals(today) || lastLogin.equals(today.minusDays(1));
    }

    /**
     * Tính số ngày bỏ lỡ kể từ lần đăng nhập cuối.
     */
    public int getMissedDays(User user) {
        if (user.getLastLoginDate() == null) {
            return 0;
        }

        LocalDate lastLogin = user.getLastLoginDate().toLocalDate();
        LocalDate today = LocalDate.now();

        if (lastLogin.equals(today)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(lastLogin, today) - 1;
    }

    /**
     * Reset streak về 0.
     */
    @Transactional
    public void resetStreak(User user) {
        user.setStreak(0);
        user.setLastLoginDate(null);
        userRepository.save(user);
    }
}