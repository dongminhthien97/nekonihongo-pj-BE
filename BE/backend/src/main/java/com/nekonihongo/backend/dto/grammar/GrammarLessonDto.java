// GrammarLessonDto.java
package com.nekonihongo.backend.dto.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GrammarLessonDto {
    private Integer id;
    private String title;
    private String icon;
    private List<GrammarPointDto> grammar;
}