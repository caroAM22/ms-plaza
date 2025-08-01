package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IDishRepository extends JpaRepository<DishEntity, String> {
    boolean existsByNameAndRestaurantId(String name, String restaurantId);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE DishEntity d SET d.price = :price, d.description = :description WHERE d.id = :id")
    int updatePriceAndDescription(@Param("id") String id, @Param("price") Integer price, @Param("description") String description);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE DishEntity d SET d.active = :active WHERE d.id = :id")
    int updateActive(@Param("id") String id, @Param("active") Boolean active);
    
    Page<DishEntity> findByRestaurantIdAndCategoryIdAndActiveIsTrue(String restaurantId, Integer categoryId, Pageable pageable);
    
    Page<DishEntity> findByRestaurantIdAndActiveIsTrue(String restaurantId, Pageable pageable);
} 