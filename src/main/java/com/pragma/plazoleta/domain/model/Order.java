package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private UUID clientId;
    private LocalDateTime date;
    private OrderStatus status;
    private UUID chefId;
    private UUID restaurantId;
    private String securityPin;
    private List<OrderDish> orderDishes;
} 