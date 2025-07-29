package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> getById(UUID id);
    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);
    Dish updateDish(Dish dish);
    Dish updateDishActive(Dish dish);
    boolean existsById(UUID id);
    Page<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, Pageable pageable);
} 