// src/main/java/com/nekonihongo/backend/security/UserPrincipal.java (FULL CODE USERPRINCIPAL HOÀN CHỈNH)

package com.nekonihongo.backend.security;

import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.entity.User.Role;
import com.nekonihongo.backend.entity.User.Status;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final User user;

    // Constructor nhận entity User
    public UserPrincipal(User user) {
        this.user = user;
    }

    // Lấy user id (dùng trong service để getCurrentUserId)
    public Long getId() {
        return user.getId();
    }

    // Authorities: Vì User entity chỉ có 1 role (enum Role), trả về list với 1
    // authority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().name(); // "USER" hoặc "ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // Hoặc user.getEmail() nếu dùng email làm username
    }

    // Các method kiểm tra trạng thái tài khoản
    @Override
    public boolean isAccountNonExpired() {
        return true; // Không expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() != Status.BANNED; // BANNED thì locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password không expire
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == Status.ACTIVE; // Chỉ ACTIVE mới enabled
    }

    // Optional: Getter cho entity User nếu cần
    public User getUser() {
        return user;
    }
}