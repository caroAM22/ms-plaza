package com.pragma.plazoleta.application.dto.response;

import lombok.Data;

@Data
public class RestaurantMenuResponse {
    private String id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private Integer categoryId;
} 