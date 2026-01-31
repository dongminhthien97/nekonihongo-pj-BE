// ExerciseDTO.java
package com.nekonihongo.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer lessonNumber;
    private Integer totalQuestions;
    private List<QuestionDTO> questions;
}