package com.wellwork.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeneratedMessageRequestDTO {

    @NotNull(message = "O ID do check-in é obrigatório")
    private Long checkInId;
}
