// KatakanaRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Katakana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KatakanaRepository extends JpaRepository<Katakana, Integer> {
    Optional<Katakana> findByCharacter(String character);
}