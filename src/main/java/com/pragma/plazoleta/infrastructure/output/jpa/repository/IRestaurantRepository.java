package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, String> {
    boolean existsByNit(long nit);
    boolean existsByName(String name);
    
    @Query("SELECT r FROM RestaurantEntity r ORDER BY r.name ASC")
    Page<RestaurantEntity> findAllOrderedByName(Pageable pageable);
} 