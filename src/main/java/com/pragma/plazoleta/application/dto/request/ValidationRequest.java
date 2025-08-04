package com.pragma.plazoleta.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRequest {
    @Pattern(regexp = "^\\d{6}$", message = "El PIN debe tener exactamente 6 dígitos")
    @NotBlank(message = "El PIN es requerido")
    private String pin;
} 