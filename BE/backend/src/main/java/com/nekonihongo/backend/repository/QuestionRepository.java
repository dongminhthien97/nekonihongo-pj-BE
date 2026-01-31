// QuestionRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExercise_IdOrderByDisplayOrder(Long exerciseId);
}