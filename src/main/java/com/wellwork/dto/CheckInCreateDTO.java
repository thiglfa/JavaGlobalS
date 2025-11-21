package com.wellwork.dto;

import com.wellwork.model.enums.EnergyLevel;
import com.wellwork.model.enums.Mood;
import jakarta.validation.constraints.NotNull;

public record CheckInCreateDTO(@NotNull Mood mood, @NotNull EnergyLevel energyLevel, String notes) {}
