package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Restaurant;
import java.util.Optional;

public interface IRestaurantServicePort {
    Restaurant createRestaurant(Restaurant restaurant, String role);
    Optional<Restaurant> getById(String id);
} 