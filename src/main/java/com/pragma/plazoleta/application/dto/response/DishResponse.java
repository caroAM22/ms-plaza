package com.pragma.plazoleta.application.dto.response;

import lombok.Data;

@Data
public class DishResponse {
    private String id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private int categoryId;
    private String restaurantId;
    private boolean active;
} 