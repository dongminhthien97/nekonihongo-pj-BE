// HiraganaRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Hiragana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HiraganaRepository extends JpaRepository<Hiragana, Integer> {
    Optional<Hiragana> findByCharacter(String character);
}