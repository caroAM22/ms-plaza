package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    private UUID id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private Integer categoryId;
    private UUID restaurantId;
    private boolean active;
} 