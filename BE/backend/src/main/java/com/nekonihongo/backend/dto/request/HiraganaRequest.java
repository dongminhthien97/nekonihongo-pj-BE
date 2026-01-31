// HiraganaRequest.java
package com.nekonihongo.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiraganaRequest {
    @NotBlank(message = "Character is required")
    @JsonProperty("character")
    private String character;

    @NotBlank(message = "Romanji is required")
    private String romanji;

    @NotBlank(message = "Unicode is required")
    private String unicode;

    @NotNull(message = "Stroke order is required")
    @JsonProperty("stroke_order")
    private Integer strokeOrder;
}