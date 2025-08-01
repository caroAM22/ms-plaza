package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String id;
    private String clientId;
    private LocalDateTime date;
    private String status;
    private String chefId;
    private String restaurantId;
    private List<OrderDishResponse> orderDishes;
} 