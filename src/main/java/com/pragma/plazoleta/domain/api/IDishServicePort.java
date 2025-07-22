package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface IDishServicePort {
    Dish createDish(String userId, Dish dish, Restaurant restaurantOwner);
    Dish getById(String id);
    Dish updateDish(Dish dish, Integer price, String description);
} 