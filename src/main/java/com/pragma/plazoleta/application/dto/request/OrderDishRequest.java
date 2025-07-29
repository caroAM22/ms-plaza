package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDishRequest {
    @NotBlank(message = "Dish ID is required")
    private String dishId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
} 