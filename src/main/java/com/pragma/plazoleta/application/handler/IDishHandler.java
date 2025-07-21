package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.dish.DishRequestDto;
import com.pragma.plazoleta.application.dto.dish.DishResponseDto;

public interface IDishHandler {
    DishResponseDto createDish(String userId, DishRequestDto dto);
} 