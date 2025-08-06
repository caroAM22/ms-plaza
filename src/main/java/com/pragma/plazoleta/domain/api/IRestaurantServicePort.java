package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;

import java.util.List;
import java.util.UUID;

public interface IRestaurantServicePort {
    int MAXIMUM_PHONE_LENGTH = 13;
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(UUID id);
    Page<Restaurant> getAllRestaurants(Pageable pageable);
    boolean existsById(UUID id);
    List<OrderSummary> getRestaurantOrdersSummary(UUID restaurantId);
    List<EmployeeAverageTime> getRestaurantEmployeesRanking(UUID restaurantId);
} 