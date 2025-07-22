package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.application.mapper.IRestaurantRequestMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantResponseMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {
    private final IRestaurantRequestMapper requestMapper;
    private final IRestaurantResponseMapper responseMapper;
    private final IRestaurantServicePort restaurantServicePort;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest dto) {
        Restaurant restaurant = requestMapper.toModel(dto);
        if (restaurant.getId() == null || restaurant.getId().isEmpty()) {
            restaurant.setId(UUID.randomUUID().toString());
        }
        Restaurant created = restaurantServicePort.createRestaurant(restaurant);
        return responseMapper.toDto(created);
    }
} 