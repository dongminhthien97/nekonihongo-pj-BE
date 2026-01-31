package com.nekonihongo.backend.dto;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private String userAnswer;
}