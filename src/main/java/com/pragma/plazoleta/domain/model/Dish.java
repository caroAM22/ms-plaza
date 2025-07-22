package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    private String id;
    private String name;
    private Integer price;
    private String description;
    private String imageUrl;
    private int categoryId;
    private String restaurantId;
    private boolean active;
} 