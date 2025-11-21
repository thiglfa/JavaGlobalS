package com.wellwork.dto;

import com.wellwork.model.enums.EnergyLevel;
import com.wellwork.model.enums.Mood;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequestDTO {

    @NotNull(message = "O usuário é obrigatório")
    private Long userId;

    @NotNull(message = "O humor é obrigatório")
    private Mood mood;

    @NotNull(message = "O nível de energia é obrigatório")
    private EnergyLevel energyLevel;

    private String notes;
}
