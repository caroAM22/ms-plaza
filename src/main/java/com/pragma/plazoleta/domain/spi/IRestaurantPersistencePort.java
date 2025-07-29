package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IRestaurantPersistencePort {
    Restaurant save(Restaurant restaurant);
    boolean existsByNit(long nit);
    boolean existsByName(String name);
    Optional<Restaurant> findById(UUID id);
    boolean existsById(UUID id);
    Page<Restaurant> findAll(Pageable pageable);
} 