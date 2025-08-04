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
    @Pattern(regexp = "^\\d{6}$", message = "The PIN must have exactly 6 digits")
    @NotBlank(message = "The PIN is required")
    private String pin;
} 