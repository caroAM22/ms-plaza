package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;

public interface IDishHandler {
    DishResponse createDish(String userId, String role, DishRequest dto);
    DishResponse updateDish(String userId, String role, String dishId, DishUpdateRequest dto);
} 