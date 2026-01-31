package com.nekonihongo.backend.dto;

import com.nekonihongo.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password; // chỉ cần khi tạo mới

    private String username;
    private String fullName;
    private String avatarUrl;
    private User.Role role = User.Role.USER;
    private Integer level = 1;
    private Integer points = 0;
    private Integer streak = 0;
    private Integer longestStreak = 0;
    private User.Status status;
}