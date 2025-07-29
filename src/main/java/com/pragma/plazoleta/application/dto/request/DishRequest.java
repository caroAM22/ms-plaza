package com.pragma.plazoleta.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishRequest {
    @NotBlank(message = "Dish name is required")
    private String name;

    @Min(value = 1, message = "Dish price must be a positive integer")
    private int price;

    @NotBlank(message = "Dish description is required")
    private String description;

    @NotBlank(message = "Dish imageUrl is required")
    private String imageUrl;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Restaurant id is required")
    private UUID restaurantId;
} 