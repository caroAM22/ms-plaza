package com.pragma.plazoleta.application.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
} 