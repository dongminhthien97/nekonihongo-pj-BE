package com.nekonihongo.backend.dto;

import com.nekonihongo.backend.entity.GrammarQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDTO {
    private Long id;
    private Integer lessonId;
    private String example; // 例 grouping
    private String type; // Khớp với entity: getType()
    private String text; // Khớp với entity: getText()
    private List<String> options;
    private Integer points;

    // Factory method từ entity
    public static QuestionResponseDTO fromEntity(GrammarQuestion question) {
        if (question == null)
            return null;

        return QuestionResponseDTO.builder()
                .id(question.getId())
                .lessonId(question.getLessonId())
                .example(question.getExample())
                .type(question.getType() != null ? question.getType().name() : null)
                .text(question.getText())
                .options(question.getOptions())
                .points(question.getPoints())
                .build();
    }
}
