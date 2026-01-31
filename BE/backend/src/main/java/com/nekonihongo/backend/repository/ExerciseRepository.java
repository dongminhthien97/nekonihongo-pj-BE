// N5VocabularyRepository đã có

// ExerciseRepository.java
package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.Exercise;
import com.nekonihongo.backend.enums.CategoryType;
import com.nekonihongo.backend.enums.JlptLevelType;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByCategory_NameAndLevel_LevelOrderByLessonNumber(
            CategoryType categoryName,
            JlptLevelType levelName);

    // Lấy bài tập theo category (không cần level)
    List<Exercise> findByCategory_NameOrderByLessonNumber(String categoryName);
}