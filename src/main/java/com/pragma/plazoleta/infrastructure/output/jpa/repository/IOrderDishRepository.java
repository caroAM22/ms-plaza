package com.pragma.plazoleta.infrastructure.output.jpa.repository;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderDishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.OrderDishId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderDishRepository extends JpaRepository<OrderDishEntity, OrderDishId> {
    
    List<OrderDishEntity> findByOrderId(String orderId);
} 