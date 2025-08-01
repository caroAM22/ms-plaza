package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.application.mapper.IRestaurantMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {
    private final IRestaurantMapper restaurantMapper;
    private final IRestaurantServicePort restaurantServicePort;

    @Override
    public RestaurantResponse createRestaurant(RestaurantRequest request) {
        Restaurant restaurant = restaurantMapper.toRestaurant(request);
        restaurant.setId(UUID.randomUUID());
        Restaurant created = restaurantServicePort.createRestaurant(restaurant);
        return restaurantMapper.toRestaurantResponse(created);
    }

    @Override
    public Page<RestaurantListResponse> getAllRestaurants(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Restaurant> restaurants = restaurantServicePort.getAllRestaurants(pageRequest);
        return restaurants.map(restaurantMapper::toRestaurantListResponse);
    }

    @Override
    public RestaurantResponse getRestaurantById(String restaurantId) {
        Restaurant restaurant = restaurantServicePort.getRestaurantById(UUID.fromString(restaurantId));
        return restaurantMapper.toRestaurantResponse(restaurant);
    }
} 