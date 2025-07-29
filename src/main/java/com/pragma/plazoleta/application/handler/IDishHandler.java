package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishActiveUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;

public interface IDishHandler {
    DishResponse createDish(DishRequest dishRequest);
    DishResponse updateDish(String dishId, DishUpdateRequest dishRequest);
    DishResponse updateDishActive(String dishId, DishActiveUpdateRequest dishRequest);
} 