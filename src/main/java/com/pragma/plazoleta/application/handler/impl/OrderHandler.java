package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.request.ValidationRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.application.mapper.IOrderMapper;
import com.pragma.plazoleta.application.mapper.INotificationMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.NotificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler {
    private final IOrderServicePort orderServicePort;
    private final IOrderMapper orderMapper;
    private final INotificationMapper notificationMapper;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        order.setId(UUID.randomUUID());
        return orderMapper.toOrderResponse(orderServicePort.createOrder(order));
    }

    @Override
    public Page<OrderResponse> getOrdersByStatusAndRestaurant(String status, String restaurantId, int page, int size) {
        OrderStatus orderStatus = OrderStatus.fromString(status);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderServicePort.getOrdersByStatusAndRestaurant(orderStatus, UUID.fromString(restaurantId), pageable);
        
        return orders.map(orderMapper::toOrderResponse);
    }

    @Override
    public OrderResponse assignOrderToEmployee(String orderId) {
        Order order = orderServicePort.assignOrderToEmployee(UUID.fromString(orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public NotificationResponse sendNotificationToCustomer(String orderId) {
        NotificationResult result = orderServicePort.sendNotificationToCustomer(UUID.fromString(orderId));
        return notificationMapper.toNotificationResponse(result);
    }

    @Override
    public OrderResponse updateSecurityPin(String orderId) {
        Order order = orderServicePort.updateSecurityPin(UUID.fromString(orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse updateOrderToDelivered(String orderId, ValidationRequest validationRequest) {
        Order order = orderServicePort.updateOrderToDelivered(UUID.fromString(orderId), validationRequest.getPin());
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(String orderId) {
        Order order = orderServicePort.cancelOrder(UUID.fromString(orderId));
        return orderMapper.toOrderResponse(order);
    }
} 