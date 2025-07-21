package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.dish.DishRequestDto;
import com.pragma.plazoleta.application.dto.dish.DishResponseDto;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.mapper.IDishRequestMapper;
import com.pragma.plazoleta.application.mapper.IDishResponseMapper;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.usecase.CategoryUseCase;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import com.pragma.plazoleta.domain.usecase.RestaurantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishHandler implements IDishHandler {
    private final DishUseCase dishUseCase;
    private final CategoryUseCase categoryUseCase;
    private final RestaurantUseCase restaurantUseCase;
    private final IDishRequestMapper requestMapper;
    private final IDishResponseMapper responseMapper;

    @Override
    public DishResponseDto createDish(String userId, DishRequestDto dto) {
        Category category = categoryUseCase.getByName(dto.getCategoryName());
        Restaurant restaurant = restaurantUseCase.getById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        Dish dish = requestMapper.toModel(dto);
        dish.setId(UUID.randomUUID().toString());
        dish.setCategoryId(category.getId() != null ? category.getId() : 0);
        dish.setActive(true);
        Dish created = dishUseCase.createDish(userId, dish, restaurant);
        return responseMapper.toDto(created);
    }
} 