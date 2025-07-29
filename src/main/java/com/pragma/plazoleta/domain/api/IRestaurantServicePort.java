package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface IRestaurantServicePort {
    int MAXIMUM_PHONE_LENGTH = 13;
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant getById(UUID id);
    Page<Restaurant> getAllRestaurants(Pageable pageable);
    boolean existsById(UUID id);
} 