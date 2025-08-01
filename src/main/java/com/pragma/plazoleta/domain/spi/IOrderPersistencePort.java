package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderDish;
import com.pragma.plazoleta.domain.model.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IOrderPersistencePort {
    Order saveOrder(Order order);
    List<OrderDish> saveOrderDishes(List<OrderDish> orderDishes);
    boolean hasActiveOrders(UUID clientId);
    Page<Order> findByStatusAndRestaurant(OrderStatus status, UUID restaurantId, Pageable pageable);
} 