package com.nekonihongo.backend.service.impl;

import com.nekonihongo.backend.entity.User;
import com.nekonihongo.backend.repository.UserRepository;
import com.nekonihongo.backend.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Case-insensitive lookup by username or email
        return userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, username);
    }

    public Optional<User> findByUsernameOrEmailIgnoreCase(String identifier) {
        return userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public User createUser(User user, String rawPassword) {
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setJoinDate(LocalDate.now());

        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        user.setLevel(user.getLevel() > 0 ? user.getLevel() : 1);
        user.setPoints(user.getPoints() >= 0 ? user.getPoints() : 0);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user!"));

        // Only update non-null fields
        if (updatedUser.getUsername() != null)
            existing.setUsername(updatedUser.getUsername());
        if (updatedUser.getFullName() != null)
            existing.setFullName(updatedUser.getFullName());
        if (updatedUser.getAvatarUrl() != null)
            existing.setAvatarUrl(updatedUser.getAvatarUrl());
        if (updatedUser.getRole() != null)
            existing.setRole(updatedUser.getRole());
        existing.setLevel(updatedUser.getLevel());
        existing.setPoints(updatedUser.getPoints());
        existing.setStreak(updatedUser.getStreak());
        existing.setLongestStreak(updatedUser.getLongestStreak());
        if (updatedUser.getStatus() != null)
            existing.setStatus(updatedUser.getStatus());

        return userRepository.save(existing);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User không tồn tại!");
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    public String extractUsernameFromToken(String token) {
        return token;
    }
}
