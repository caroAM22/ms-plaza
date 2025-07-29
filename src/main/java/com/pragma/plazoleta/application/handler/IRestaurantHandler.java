package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRestaurantHandler {
    RestaurantResponse createRestaurant(RestaurantRequest request);
    Page<RestaurantListResponse> getAllRestaurants(Pageable pageable);
} 