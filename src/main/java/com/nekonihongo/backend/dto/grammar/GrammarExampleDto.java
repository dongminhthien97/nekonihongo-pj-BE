// GrammarExampleDto.java
package com.nekonihongo.backend.dto.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GrammarExampleDto {
    private String japanese;
    private String vietnamese;
}