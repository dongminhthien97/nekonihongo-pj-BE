// GrammarPointDto.java
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
public class GrammarPointDto {
    private String title;
    private String meaning;
    private String explanation;
    private List<GrammarExampleDto> examples;
}