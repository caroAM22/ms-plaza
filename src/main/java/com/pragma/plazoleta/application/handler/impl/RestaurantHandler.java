package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.dto.response.EmployeeAverageTimeResponse;
import com.pragma.plazoleta.application.dto.response.OrderSummaryResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.application.mapper.IEmployeeAverageTimeMapper;
import com.pragma.plazoleta.application.mapper.IOrderSummaryMapper;
import com.pragma.plazoleta.application.mapper.IRestaurantMapper;
import com.pragma.plazoleta.domain.api.IRestaurantServicePort;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {
    private final IRestaurantMapper restaurantMapper;
    private final IRestaurantServicePort restaurantServicePort;
    private final IOrderSummaryMapper orderSummaryMapper;
    private final IEmployeeAverageTimeMapper employeeAverageTimeMapper;

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

    @Override
    public List<OrderSummaryResponse> getRestaurantOrdersSummary(String restaurantId) {
        List<OrderSummary> orderSummaries = restaurantServicePort.getRestaurantOrdersSummary(UUID.fromString(restaurantId));
        return orderSummaryMapper.toOrderSummaryResponseList(orderSummaries);
    }

    @Override
    public List<EmployeeAverageTimeResponse> getRestaurantEmployeesRanking(String restaurantId) {
        List<EmployeeAverageTime> employeeAverageTimes = restaurantServicePort.getRestaurantEmployeesRanking(UUID.fromString(restaurantId));
        return employeeAverageTimeMapper.toEmployeeAverageTimeResponseList(employeeAverageTimes);
    }
} 