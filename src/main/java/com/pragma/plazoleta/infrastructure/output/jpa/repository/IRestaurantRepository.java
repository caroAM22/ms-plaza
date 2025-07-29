package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, String> {
    boolean existsByNit(long nit);
    boolean existsByName(String name);
} 