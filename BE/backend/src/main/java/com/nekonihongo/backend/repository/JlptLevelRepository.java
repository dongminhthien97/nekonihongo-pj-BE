// src/main/java/com/nekonihongo/backend/repository/JlptLevelRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.JlptLevel;
import com.nekonihongo.backend.enums.JlptLevelType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JlptLevelRepository extends JpaRepository<JlptLevel, Integer> {

    Optional<JlptLevel> findByLevel(JlptLevelType level);

    boolean existsByLevel(JlptLevelType level);
}