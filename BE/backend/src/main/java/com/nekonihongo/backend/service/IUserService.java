// src/main/java/com/nekonihongo/backend/service/IUserService.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    /**
     * Lấy tất cả user (dùng cho admin)
     */
    List<User> findAll();

    /**
     * Lấy user theo ID
     */
    Optional<User> findById(Long id);

    /**
     * Lấy user theo email (dùng cho login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Tạo user mới (admin tạo hoặc đăng ký)
     * 
     * @param user        thông tin user
     * @param rawPassword mật khẩu chưa mã hóa
     */
    User createUser(User user, String rawPassword);

    /**
     * Cập nhật user (admin sửa thông tin)
     */
    User updateUser(Long id, User user);

    /**
     * Xóa user theo ID
     */
    void deleteById(Long id);

    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmail(String email);

    /**
     * Đếm số user theo role (dùng cho dashboard admin)
     */
    long countByRole(User.Role role);

    /**
     * Tìm user theo username
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm user theo username hoặc email (case-insensitive)
     */
    Optional<User> findByUsernameOrEmailIgnoreCase(String identifier);
}
