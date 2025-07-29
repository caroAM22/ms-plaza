package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;

public interface IOrderHandler {
    OrderResponse createOrder(OrderRequest orderRequest);
} 