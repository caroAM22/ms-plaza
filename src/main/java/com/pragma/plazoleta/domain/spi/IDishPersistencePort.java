package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.DomainPage;
import java.util.UUID;
import java.util.Optional;

public interface IDishPersistencePort {
    Dish save(Dish dish);
    Optional<Dish> getById(UUID id);
    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);
    boolean updateDish(Dish dish);
    boolean updateDishActive(Dish dish);
    boolean existsById(UUID id);
    DomainPage<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, int page, int size);
} 