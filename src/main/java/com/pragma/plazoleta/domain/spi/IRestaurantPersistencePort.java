package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.model.DomainPage;

import java.util.Optional;
import java.util.UUID;

public interface IRestaurantPersistencePort {
    Restaurant save(Restaurant restaurant);
    boolean existsByNit(long nit);
    boolean existsByName(String name);
    Optional<Restaurant> findById(UUID id);
    boolean existsById(UUID id);
    DomainPage<Restaurant> findAll(int page, int size);
} 