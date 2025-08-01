package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IOrderHandler {
    OrderResponse createOrder(OrderRequest orderRequest);
    Page<OrderResponse> getOrdersByStatusAndRestaurant(String status, UUID restaurantId, int page, int size);
} 