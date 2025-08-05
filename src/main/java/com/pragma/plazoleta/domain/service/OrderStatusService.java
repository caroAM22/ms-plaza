package com.pragma.plazoleta.domain.service;

import com.pragma.plazoleta.domain.exception.OrderException;
import com.pragma.plazoleta.domain.model.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {
    
    public void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.IN_PREPARATION) {
            return; 
        }
        if (currentStatus == OrderStatus.IN_PREPARATION && newStatus == OrderStatus.READY) {
            return;
        }
        if (currentStatus == OrderStatus.READY && newStatus == OrderStatus.DELIVERED) {
            return; 
        }
        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.CANCELLED) {
            return; 
        }
        
        throw new OrderException("Invalid status transition from " + currentStatus + " to " + newStatus);
    }

} 