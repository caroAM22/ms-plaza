package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.DomainPage;
import java.util.UUID;
import java.util.Optional;

public interface IDishServicePort {
    Dish createDish(Dish dish);
    Dish updateDish(UUID dishId, Optional<Integer> price, Optional<String> description);
    Dish updateDishActive(UUID dishId, Optional<Boolean> active);
    DomainPage<Dish> getDishesByRestaurant(UUID restaurantId, Optional<Integer> categoryId, int page, int size);
    boolean existsById(UUID id);
    boolean isActiveById(UUID id);
} 