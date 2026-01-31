// KatakanaDTO.java
package com.nekonihongo.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KatakanaDTO {
    private Integer id;

    @JsonProperty("`character`")
    private String character;

    private String romanji;
    private String unicode;

    @JsonProperty("stroke_order")
    private Integer strokeOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}