package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Dish getById(String id);
    Dish updateDish(Dish dish, Integer price, String description);
    boolean existsByName(String name);
} 