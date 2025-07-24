package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;

public interface IRestaurantHandler {
    RestaurantResponse createRestaurant(RestaurantRequest request, String role);
} 