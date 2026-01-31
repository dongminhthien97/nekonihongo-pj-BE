package com.nekonihongo.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mini_test_submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MiniTestSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lesson_id", nullable = false)
    private Integer lessonId;

    @Column(columnDefinition = "JSON", nullable = false)
    private String answers;

    @Column(name = "time_spent")
    @Builder.Default
    private Integer timeSpent = 0;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "feedback_at")
    private LocalDateTime feedbackAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.pending;

    @Column(name = "score")
    private Integer score; // Thêm field này

    public enum Status {
        pending,
        feedbacked
    }
}