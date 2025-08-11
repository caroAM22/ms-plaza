package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.pragma.plazoleta.domain.utils.Constants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceabilityRequest {
    @NotNull(message = "Order ID is required")
    private String orderId;
    
    @NotBlank(message = "Client ID is required")
    private String clientId;
    
    @NotBlank(message = "Client email is required")
    @Email(message = "Client email must be valid")
    private String clientEmail;
    
    @NotBlank(message = "Previous state is required")
    private String previousState;
    
    @NotBlank(message = "New state is required")
    private String newState;
    
    @NotNull(message = "Employee ID is required")
    private String employeeId;
    
    @NotBlank(message = "Employee email is required")
    @Email(message = "Employee email must be valid")
    private String employeeEmail;

    @NotNull(message = "Restaurant ID is required")
    @Pattern(regexp = Constants.UUID_PATTERN, 
             message = "Restaurant ID must be a valid UUID format")
    private String restaurantId;
} 