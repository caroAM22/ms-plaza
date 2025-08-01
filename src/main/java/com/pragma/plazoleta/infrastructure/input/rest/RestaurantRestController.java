package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantListResponse;
import com.pragma.plazoleta.application.dto.response.RestaurantMenuResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import com.pragma.plazoleta.application.handler.IDishHandler;
import com.pragma.plazoleta.application.handler.IOrderHandler;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public class RestaurantRestController {
    private final IRestaurantHandler handler;
    private final IDishHandler dishHandler;
    private final IOrderHandler orderHandler;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Create a new restaurant", description = "Creates a new restaurant with all required validations.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "NIT already exists",content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "User does not have OWNER role",content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        RestaurantResponse response = handler.createRestaurant(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "List all restaurants", description = "Lists all restaurants ordered alphabetically and paginated.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully", content = @Content(schema = @Schema(implementation = RestaurantListResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Page<RestaurantListResponse>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RestaurantListResponse> restaurants = handler.getAllRestaurants(page, size);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{restaurantId}/menu")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @Operation(summary = "Get restaurant menu", description = "Lists all active dishes from a restaurant with pagination and optional category filtering.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu retrieved successfully", content = @Content(schema = @Schema(implementation = RestaurantMenuResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Page<RestaurantMenuResponse>> getRestaurantMenu(
            @PathVariable String restaurantId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Optional<Integer> optionalCategoryId = Optional.ofNullable(categoryId);
        Page<RestaurantMenuResponse> menu = dishHandler.getRestaurantMenu(restaurantId, optionalCategoryId, page, size);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/{restaurantId}/orders")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    @Operation(summary = "Get orders by status and restaurant", description = "Gets paginated orders filtered by status for a specific restaurant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatusAndRestaurant(
            @PathVariable String restaurantId,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderHandler.getOrdersByStatusAndRestaurant(status, UUID.fromString(restaurantId), page, size);
        return ResponseEntity.ok(orders);
    }
} 