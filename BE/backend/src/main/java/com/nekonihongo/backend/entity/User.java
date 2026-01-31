//User.java
package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 50)
    private String username;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    // ⭐ CORE STATS - Chỉ giữ 3 trường chính ⭐
    @Column(nullable = false)
    @Builder.Default
    private int level = 1;

    @Column(nullable = false)
    @Builder.Default
    private int points = 0;

    @Column(nullable = false)
    @Builder.Default
    private int streak = 0;

    // Thêm 2 trường để tính streak
    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "longest_streak", nullable = true)
    @Builder.Default
    private int longestStreak = 0;

    @CreationTimestamp
    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDate joinDate;

    // ⭐ THÊM TRƯỜNG STATUS ⭐
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    public enum Role {
        USER("user"),
        ADMIN("admin");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // Enum cho status tài khoản
    public enum Status {
        ACTIVE, // Bình thường
        INACTIVE, // Bị khóa tạm thời
        BANNED // Bị cấm vĩnh viễn
    }
}