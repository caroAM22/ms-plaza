package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.request.ValidationRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;

public interface IOrderHandler {
    OrderResponse createOrder(OrderRequest orderRequest);
    Page<OrderResponse> getOrdersByStatusAndRestaurant(String status, String restaurantId, int page, int size);
    OrderResponse assignOrderToEmployee(String orderId);
    NotificationResponse sendNotificationToCustomer(String orderId);
    OrderResponse updateSecurityPin(String orderId);
    OrderResponse updateOrderToDelivered(String orderId, ValidationRequest validationRequest);
} 