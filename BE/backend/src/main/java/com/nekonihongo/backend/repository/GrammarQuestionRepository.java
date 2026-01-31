package com.nekonihongo.backend.repository;

import com.nekonihongo.backend.entity.GrammarQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarQuestionRepository extends JpaRepository<GrammarQuestion, Long> {

    List<GrammarQuestion> findByLessonId(Integer lessonId);

    boolean existsByLessonId(Integer lessonId);

    @Query("SELECT q FROM GrammarQuestion q WHERE q.lessonId = :lessonId ORDER BY q.id")
    List<GrammarQuestion> findByLessonIdOrderById(@Param("lessonId") Integer lessonId);

    @Query("SELECT COUNT(q) FROM GrammarQuestion q WHERE q.lessonId = :lessonId")
    Long countByLessonId(@Param("lessonId") Integer lessonId);

    @Query("SELECT SUM(q.points) FROM GrammarQuestion q WHERE q.lessonId = :lessonId")
    Integer sumPointsByLessonId(@Param("lessonId") Integer lessonId);
}