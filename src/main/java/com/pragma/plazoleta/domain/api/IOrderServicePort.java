package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;
import com.pragma.plazoleta.domain.model.NotificationResult;

import java.util.List;
import java.util.UUID;

public interface IOrderServicePort {
    Order createOrder(Order order);
    boolean hasActiveOrders(UUID clientId);
    DomainPage<Order> getOrdersByStatusAndRestaurant(String status, UUID restaurantId, int page, int size);
    Order assignOrderToEmployee(UUID orderId);
    NotificationResult sendNotificationToCustomer(UUID orderId);
    Order updateSecurityPin(UUID orderId);
    Order updateOrderToDelivered(UUID orderId, String pin);
    Order cancelOrder(UUID orderId);
    List<TraceabilityGrouped> getClientHistory(UUID clientId);
    List<Traceability> getOrderTraceability(UUID orderId);
} 