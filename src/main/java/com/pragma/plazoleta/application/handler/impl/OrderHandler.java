package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.application.mapper.IOrderMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
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

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        order.setId(UUID.randomUUID());
        return orderMapper.toOrderResponse(orderServicePort.createOrder(order));
    }

    @Override
    public Page<OrderResponse> getOrdersByStatusAndRestaurant(String status, UUID restaurantId, int page, int size) {
        OrderStatus orderStatus = OrderStatus.fromString(status);
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderServicePort.getOrdersByStatusAndRestaurant(orderStatus, restaurantId, pageable);
        
        return orders.map(orderMapper::toOrderResponse);
    }
} 