// src/main/java/com/nekonihongo/backend/service/AuthService.java
package com.nekonihongo.backend.service;

import com.nekonihongo.backend.dto.AuthRequest;
import com.nekonihongo.backend.dto.AuthResponse;
import com.nekonihongo.backend.dto.UserResponse;
import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final StreakService streakService;

        /**
         * Xử lý login:
         * - Kiểm tra email + password
         * - Cập nhật streak và lastLoginDate
         * - Sinh JWT + refresh token
         * - Trả về AuthResponse chứa thông tin user
         */
        public AuthResponse login(AuthRequest request) {
                // Tìm user theo email
                User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                                .orElseThrow(() -> new BadCredentialsException("Email hoặc mật khẩu sai!"));

                // Kiểm tra password
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new BadCredentialsException("Email hoặc mật khẩu sai!");
                }

                // ✅ Cập nhật streak khi đăng nhập
                streakService.updateLoginStreak(user);
                userRepository.save(user); // ghi xuống DB
                // Claims cho JWT
                var claims = new HashMap<String, Object>();
                claims.put("role", user.getRole().name());
                claims.put("userId", user.getId());

                // Sinh token
                String token = jwtService.generateToken(user.getEmail(), claims);
                String refreshToken = jwtService.generateRefreshToken(user.getEmail());

                // Trả về response
                return AuthResponse.builder()
                                .token(token)
                                .refreshToken(refreshToken)
                                .user(
                                                UserResponse.builder()
                                                                .id(user.getId())
                                                                .email(user.getEmail())
                                                                .username(user.getUsername())
                                                                .fullName(user.getFullName())
                                                                .avatarUrl(user.getAvatarUrl())
                                                                .role(user.getRole().name().toLowerCase())
                                                                .level(user.getLevel())
                                                                .points(user.getPoints())
                                                                .streak(user.getStreak())
                                                                .longestStreak(user.getLongestStreak())
                                                                .lastLoginDate(user.getLastLoginDate())
                                                                .joinDate(user.getJoinDate())
                                                                .build())
                                .build();
        }
}
