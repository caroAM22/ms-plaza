package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.OrderRequest;
import com.pragma.plazoleta.application.dto.request.ValidationRequest;
import com.pragma.plazoleta.application.dto.response.NotificationResponse;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "Create order", description = "Creates a new order for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Customer already has an active order", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderHandler.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @PatchMapping("/{orderId}/assign")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @Operation(summary = "Assign order to employee", description = "Assigns a pending order to the authenticated employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order assigned successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order ID format", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied or validation failed", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Order is not in PENDING status or already assigned", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<OrderResponse> assignOrderToEmployee(@PathVariable String orderId) {
        OrderResponse orderResponse = orderHandler.assignOrderToEmployee(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping("/{orderId}/ready")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @Operation(summary = "Update order to ready", description = "Updates the order status to READY and generates a security PIN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order ID or status", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied or validation failed", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<OrderResponse> updateSecurityPin(
            @PathVariable String orderId) {
        OrderResponse orderResponse = orderHandler.updateSecurityPin(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping("/{orderId}/delivered")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @Operation(summary = "Update order to delivered", description = "Updates the order status to DELIVERED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order ID or status", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied or validation failed", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<OrderResponse> updateOrderToDelivered(
            @PathVariable String orderId,
            @Valid @RequestBody ValidationRequest validationRequest) {
        OrderResponse orderResponse = orderHandler.updateOrderToDelivered(orderId, validationRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping("/{orderId}/notification")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @Operation(summary = "Send notification to customer", description = "Sends notification to customer when order is ready")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification sent successfully", content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order ID or status", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied or validation failed", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<NotificationResponse> sendNotificationToCustomer(
            @PathVariable String orderId) {
        NotificationResponse notificationResponse = orderHandler.sendNotificationToCustomer(orderId);
        return ResponseEntity.ok(notificationResponse);
    }
} 