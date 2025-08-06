package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.dto.response.EmployeeAverageTimeResponse;
import com.pragma.plazoleta.application.dto.response.OrderSummaryResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListResponse;

import java.util.List;

import org.springframework.data.domain.Page;

public interface IRestaurantHandler {
    RestaurantResponse createRestaurant(RestaurantRequest request);
    Page<RestaurantListResponse> getAllRestaurants(int page, int size);
    RestaurantResponse getRestaurantById(String restaurantId);
    List<OrderSummaryResponse> getRestaurantOrdersSummary(String restaurantId);
    List<EmployeeAverageTimeResponse> getRestaurantEmployeesRanking(String restaurantId);
} 