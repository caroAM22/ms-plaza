package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.DomainPage;

import com.pragma.plazoleta.domain.model.EmployeeAverageTime;
import com.pragma.plazoleta.domain.model.OrderSummary;

import java.util.List;
import java.util.UUID;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(UUID id);
    DomainPage<Restaurant> getAllRestaurants(int page, int size);
    boolean existsById(UUID id);
    List<OrderSummary> getRestaurantOrdersSummary(UUID restaurantId);
    List<EmployeeAverageTime> getRestaurantEmployeesRanking(UUID restaurantId);
} 