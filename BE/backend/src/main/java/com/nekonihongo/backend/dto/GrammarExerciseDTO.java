// GrammarExerciseDTO.java (dùng cho list bài)
package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarExerciseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer lessonNumber;
    private Integer totalPatterns;
    private List<GrammarPatternDTO> patterns;
}