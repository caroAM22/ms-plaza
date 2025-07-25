package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    Restaurant save(Restaurant restaurant);
    boolean existsByNit(long nit);
    boolean existsByName(String name);
    Optional<Restaurant> findById(String id);
} 