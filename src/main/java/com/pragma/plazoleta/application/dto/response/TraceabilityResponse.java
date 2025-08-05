package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceabilityResponse {
    private String id;
    private String orderId;
    private String clientId;
    private String clientEmail;
    private LocalDateTime date;
    private String previousState;
    private String newState;
    private String employeeId;
    private String employeeEmail;
    private String restaurantId;
} 