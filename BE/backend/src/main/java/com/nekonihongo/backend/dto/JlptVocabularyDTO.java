package com.nekonihongo.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JlptVocabularyDTO {
    private String level;
    private String stt;
    private String tuVung;
    private String hanTu;
    private String tiengViet;
    private String viDu;
}