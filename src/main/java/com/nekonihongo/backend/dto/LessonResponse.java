// src/main/java/com/nekonihongo/backend/dto/LessonResponse.java
package com.nekonihongo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Integer id;
    private String title;
    private String icon;
    private List<WordResponse> words;
}