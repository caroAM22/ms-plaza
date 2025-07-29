package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IDishRepository extends JpaRepository<DishEntity, String> {
    boolean existsByNameAndRestaurantId(String name, String restaurantId);
    
    @Modifying
    @Query("UPDATE DishEntity d SET d.price = :price, d.description = :description WHERE d.id = :id")
    void updatePriceAndDescription(@Param("id") String id, @Param("price") Integer price, @Param("description") String description);
    
    @Modifying
    @Query("UPDATE DishEntity d SET d.active = :active WHERE d.id = :id")
    void updateActive(@Param("id") String id, @Param("active") Boolean active);
} 