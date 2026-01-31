// src/main/java/com/nekonihongo/backend/dto/WordResponse.java
package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordResponse {
    private String japanese;
    private String kanji;
    private String vietnamese;
    private String category;
}