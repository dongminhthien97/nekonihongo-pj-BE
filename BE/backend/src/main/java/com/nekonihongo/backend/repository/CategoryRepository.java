// src/main/java/com/nekonihongo/backend/repository/CategoryRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Category;
import com.nekonihongo.backend.enums.CategoryType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByName(CategoryType name);

    // Custom method nếu cần
    boolean existsByName(CategoryType name);
}