package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Traceability {
    private UUID id;
    private UUID orderId;
    private UUID clientId;
    private String clientEmail;
    private LocalDateTime date;
    private String previousState;
    private String newState;
    private UUID employeeId;
    private String employeeEmail;
    private UUID restaurantId;
} 