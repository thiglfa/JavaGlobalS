package com.wellwork.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class GeneratedMessageResponseDTO {

    private Long id;
    private Long checkInId;
    private String message;
    private Double confidence;
    private Instant generatedAt;
}
