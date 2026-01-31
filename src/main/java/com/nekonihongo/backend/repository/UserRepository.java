package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user bằng email (dùng cho login)
    Optional<User> findByEmailIgnoreCase(String email);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmailIgnoreCase(String email);

    // Tìm user bằng username
    Optional<User> findByUsernameIgnoreCase(String username);

    // Đếm số user theo role (dùng cho dashboard admin)
    long countByRole(User.Role role);

    // Tìm user bằng username hoặc email (case-insensitive)
    Optional<User> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    // Query reset streak cho user không hoạt động
    @Modifying
    @Query("UPDATE User u SET u.streak = 0 WHERE u.lastLoginDate < :cutoffDate AND u.streak > 0")
    int resetStreaksForInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    Optional<User> findByUsername(String username);

}
