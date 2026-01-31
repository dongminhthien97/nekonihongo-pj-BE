package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grammar_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GrammarQuestion {

    public enum QuestionType {
        fill_blank,
        multiple_choice,
        rearrange
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_id", nullable = false)
    private Integer lessonId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('fill_blank', 'multiple_choice', 'rearrange')")
    private QuestionType type;

    // example (例) để nhóm các câu theo mẫu ví dụ
    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * FIX CHÍNH: Thay đổi từ SqlTypes.JSON sang SqlTypes.LONGVARCHAR
     * nếu bạn muốn Hibernate tự động chuyển List<String> thành chuỗi JSON và lưu
     * vào cột TEXT.
     * Cách này giúp vượt qua lỗi Schema Validation mà không cần sửa Database.
     */
    @Column(name = "options", columnDefinition = "TEXT")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private List<String> options;

    // Fix: Khớp với cột 'correct_answer' kiểu TEXT trong DB (thay vì length=255)
    @Column(name = "correct_answer", columnDefinition = "TEXT", nullable = false)
    private String correctAnswer;

    @Column
    @Builder.Default
    private Integer points = 10;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship với GrammarLesson
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", insertable = false, updatable = false)
    @ToString.Exclude
    private GrammarLesson lesson;
}