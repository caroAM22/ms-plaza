package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

public interface IDishServicePort {
    Dish createDish(Dish dish, UUID restaurantOwnerId);
    Dish getById(UUID id);
    Dish updateDish(Dish dish, UUID restaurantOwnerId, Optional<Integer> price, Optional<String> description);
    Dish updateDishActive(Dish dish, UUID restaurantOwnerId, Optional<Boolean> active);
    Page<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, Pageable pageable);
} 