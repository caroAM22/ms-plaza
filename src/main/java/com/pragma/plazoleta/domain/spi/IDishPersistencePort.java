package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;
import java.util.UUID;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> getById(UUID id);
    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);
    Dish updateDish(Dish dish);
    Dish updateDishActive(Dish dish);
} 