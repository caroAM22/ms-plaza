package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Order;

import java.util.UUID;

public interface IOrderServicePort {
    Order createOrder(Order order);
    boolean hasActiveOrders(UUID clientId);
} 