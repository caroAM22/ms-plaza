package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.request.ValidationRequest;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityGroupedResponse;
import com.pragma.plazoleta.application.dto.response.TraceabilityResponse;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.application.mapper.IOrderMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityGroupedMapper;
import com.pragma.plazoleta.application.mapper.ITraceabilityMapper;
import com.pragma.plazoleta.application.mapper.INotificationMapper;
import com.pragma.plazoleta.domain.api.IOrderServicePort;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.DomainPage;
import com.pragma.plazoleta.domain.model.Traceability;
import com.pragma.plazoleta.domain.model.TraceabilityGrouped;
import com.pragma.plazoleta.domain.model.NotificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler {
    private final IOrderServicePort orderServicePort;
    private final IOrderMapper orderMapper;
    private final INotificationMapper notificationMapper;
    private final ITraceabilityGroupedMapper traceabilityGroupedMapper;
    private final ITraceabilityMapper traceabilityMapper;

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toOrder(orderRequest);
        return orderMapper.toOrderResponse(orderServicePort.createOrder(order));
    }

    @Override
    public Page<OrderResponse> getOrdersByStatusAndRestaurant(String status, String restaurantId, int page, int size) {
        DomainPage<Order> domainPage = orderServicePort.getOrdersByStatusAndRestaurant(status, UUID.fromString(restaurantId), page, size);
        
        // Convert DomainPage to Spring Page
        PageRequest pageRequest = PageRequest.of(page, size);
        return new PageImpl<>(
            domainPage.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList(),
            pageRequest,
            domainPage.getTotalElements()
        );
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

    @Override
    public List<TraceabilityGroupedResponse> getClientHistory(String clientId) {
        List<TraceabilityGrouped> traceabilityGroupedList = orderServicePort.getClientHistory(UUID.fromString(clientId));
        return traceabilityGroupedMapper.toTraceabilityGroupedResponseList(traceabilityGroupedList);
    }

    @Override
    public List<TraceabilityResponse> getOrderTraceability(String orderId) {
        List<Traceability> traceabilityList = orderServicePort.getOrderTraceability(UUID.fromString(orderId));
        return traceabilityMapper.toTraceabilityResponseList(traceabilityList);
    }
} 