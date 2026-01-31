package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitTestRequestDTO {
    private Long userId;
    private Integer lessonId;
    private Map<String, Object> answers; // Frontend gửi Map
    private Integer timeSpent;
    private LocalDateTime submittedAt;
}