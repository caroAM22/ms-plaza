package com.pragma.plazoleta.application.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class DishActiveUpdateRequest {
    @NotNull(message = "The 'active' field is required")
    private Boolean active;
} 