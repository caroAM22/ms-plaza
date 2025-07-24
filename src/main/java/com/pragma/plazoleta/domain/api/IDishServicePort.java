package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;

public interface IDishServicePort {
    Dish createDish(String userId, String role, Dish dish, String restaurantOwnerId);
    Dish getById(String id);
    Dish updateDish(Dish dish, String restaurantOwnerId, String userId, String role, Integer price, String description);
} 