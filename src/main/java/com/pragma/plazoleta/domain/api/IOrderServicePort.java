package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.NotificationResult;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderServicePort {
    Order getOrderById(UUID orderId);
    Order createOrder(Order order);
    boolean hasActiveOrders(UUID clientId);
    Page<Order> getOrdersByStatusAndRestaurant(OrderStatus status, UUID restaurantId, Pageable pageable);
    Order assignOrderToEmployee(UUID orderId);
    NotificationResult sendNotificationToCustomer(UUID orderId);
    Order updateSecurityPin(UUID orderId);
    Order updateOrderToDelivered(UUID orderId, String pin);
} 