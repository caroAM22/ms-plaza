package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.RestaurantRequest;
import com.pragma.plazoleta.application.dto.response.RestaurantResponse;
import com.pragma.plazoleta.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public class RestaurantRestController {
    private final IRestaurantHandler handler;

    @PostMapping
    @Operation(summary = "Create a new restaurant", description = "Creates a new restaurant with all required validations.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content(schema = @Schema(implementation = RestaurantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "NIT already exists"),
        @ApiResponse(responseCode = "403", description = "User does not have OWNER role")
    })
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("");
        RestaurantResponse response = handler.createRestaurant(dto, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 