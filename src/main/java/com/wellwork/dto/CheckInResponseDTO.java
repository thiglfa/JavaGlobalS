package com.wellwork.dto;

import com.wellwork.model.enums.EnergyLevel;
import com.wellwork.model.enums.Mood;
import lombok.Data;

import java.time.Instant;

@Data
public class CheckInResponseDTO {

    private Long id;
    private Long userId;
    private Mood mood;
    private EnergyLevel energyLevel;
    private String notes;
    private Instant createdAt;
}
