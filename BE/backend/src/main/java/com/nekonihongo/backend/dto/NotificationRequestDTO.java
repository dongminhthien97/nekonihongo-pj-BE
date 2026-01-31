package com.nekonihongo.backend.dto;

import lombok.Data;

@Data
public class NotificationRequestDTO {
    private Long userId;
    private String type;
    private String title;
    private String message;
    private Long relatedId;
}