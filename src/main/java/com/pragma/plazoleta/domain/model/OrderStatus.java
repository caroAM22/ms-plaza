package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.exception.OrderException;

public enum OrderStatus {
    PENDING,
    IN_PREPARATION,
    READY,
    CANCELLED,
    DELIVERED;

    public static OrderStatus fromString(String text) {
        try {
            return OrderStatus.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OrderException("Invalid order status: " + text);
        }
    }
} 