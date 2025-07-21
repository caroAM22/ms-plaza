package com.pragma.plazoleta.application.dto.dish;

import lombok.Data;

@Data
public class DishResponseDto {
    private String id;
    private String name;
    private int price;
    private String description;
    private String imageUrl;
    private int categoryId;
    private String restaurantId;
    private boolean active;
} 