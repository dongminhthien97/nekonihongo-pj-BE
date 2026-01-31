// src/main/java/com/nekonihongo/backend/dto/GrammarCountDTO.java
package com.nekonihongo.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarCountDTO {
    private String level;
    private Long count;
}