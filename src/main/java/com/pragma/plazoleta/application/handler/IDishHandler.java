package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishActiveUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantMenuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface IDishHandler {
    DishResponse createDish(DishRequest dishRequest);
    DishResponse updateDish(String dishId, DishUpdateRequest dishRequest);
    DishResponse updateDishActive(String dishId, DishActiveUpdateRequest dishRequest);
    Page<RestaurantMenuResponse> getRestaurantMenu(String restaurantId, Optional<Integer> categoryId, Pageable pageable);
} 