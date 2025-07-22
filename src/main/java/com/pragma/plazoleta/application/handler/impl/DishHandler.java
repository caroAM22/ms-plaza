package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.mapper.IDishRequestMapper;
import com.pragma.plazoleta.application.mapper.IDishResponseMapper;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishHandler implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final ICategoryServicePort categoryServicePort;
    private final IRestaurantServicePort restaurantServicePort;
    private final IDishRequestMapper requestMapper;
    private final IDishResponseMapper responseMapper;

    @Override
    public DishResponse createDish(String userId, DishRequest dto) {
        Category category = categoryServicePort.getByName(dto.getCategoryName());
        Restaurant restaurant = restaurantServicePort.getById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        Dish dish = requestMapper.toModel(dto);
        dish.setId(UUID.randomUUID().toString());
        dish.setCategoryId(category.getId() != null ? category.getId() : 0);
        dish.setActive(true);
        Dish created = dishServicePort.createDish(userId, dish, restaurant);
        return responseMapper.toDto(created);
    }

    @Override
    public DishResponse updateDish(String userId, String dishId, DishUpdateRequest dto) {
        Dish dish = dishServicePort.getById(dishId);
        Restaurant restaurant = restaurantServicePort.getById(dish.getRestaurantId())
                .orElseThrow(() -> new DomainException("Restaurant not found"));
        
        if (!userId.equals(restaurant.getOwnerId())) {
            throw new DomainException("Only the restaurant owner can update dishes");
        }
        Dish updated = dishServicePort.updateDish(dish, dto.getPrice(), dto.getDescription());
        
        return responseMapper.toDto(updated);
    }
} 