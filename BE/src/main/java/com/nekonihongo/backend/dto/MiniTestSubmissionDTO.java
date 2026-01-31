package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniTestSubmissionDTO {
    private Long id;
    private Long userId;

    // Thêm thông tin user
    private String userName;
    private String userEmail;

    private Long lessonId;
    private String lessonTitle; // Thêm field lessonTitle

    private LocalDateTime submittedAt;
    private String feedback;
    private LocalDateTime feedbackAt;
    private String status;
    private Integer score;
    private Integer timeSpent;
    private List<AnswerDTO> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerDTO {
        private Long questionId;
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
    }
}