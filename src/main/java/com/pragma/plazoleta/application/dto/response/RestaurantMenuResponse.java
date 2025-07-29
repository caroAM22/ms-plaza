package com.pragma.plazoleta.application.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class RestaurantMenuResponse {
    private UUID id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private Integer categoryId;
} 