package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishActiveUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantMenuResponse;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.mapper.IDishMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantMenuMapper;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.api.ICategoryServicePort;
import com.pragma.plazoleta.domain.api.IDishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishHandler implements IDishHandler {
    private final IDishServicePort dishServicePort;
    private final ICategoryServicePort categoryServicePort;
    private final IDishMapper dishMapper;
    private final IRestaurantMenuMapper restaurantMenuMapper;

    @Override
    public DishResponse createDish(DishRequest dishRequest) {
        Dish dish = dishMapper.toDish(dishRequest);
        Integer idCategory = categoryServicePort.getByName(dishRequest.getCategoryName()).getId();
        dish.setId(UUID.randomUUID());
        dish.setCategoryId(idCategory);
        Dish saved = dishServicePort.createDish(dish);
        
        return dishMapper.toDishResponse(saved);
    }

    @Override
    public DishResponse updateDish(String dishId, DishUpdateRequest dishRequest) {   
        Dish dish = dishServicePort.getById(UUID.fromString(dishId));
        Dish updated = dishServicePort.updateDish(dish, 
            Optional.ofNullable(dishRequest.getPrice()), 
            Optional.ofNullable(dishRequest.getDescription()));
        
        return dishMapper.toDishResponse(updated);
    }

    @Override
    public DishResponse updateDishActive(String dishId, DishActiveUpdateRequest dishRequest) {
        Dish dish = dishServicePort.getById(UUID.fromString(dishId));
        Dish updated = dishServicePort.updateDishActive(dish, 
            Optional.of(dishRequest.getActive()));
        
        return dishMapper.toDishResponse(updated);
    }

    @Override
    public Page<RestaurantMenuResponse> getRestaurantMenu(String restaurantId, Optional<Integer> categoryId, Pageable pageable) {
        UUID restaurantUUID = UUID.fromString(restaurantId);
        Page<Dish> dishes = dishServicePort.getDishesByRestaurant(restaurantUUID, categoryId, pageable);
        return dishes.map(restaurantMenuMapper::toRestaurantMenuResponse);
    }
} 