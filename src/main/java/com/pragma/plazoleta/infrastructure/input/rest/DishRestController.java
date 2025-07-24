package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.response.DishResponse;
import com.pragma.plazoleta.application.handler.IDishHandler;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management endpoints")
public class DishRestController {
    private final IDishHandler handler;

    @PostMapping
    @Operation(summary = "Create a new dish", description = "Creates a new dish. Only the restaurant owner can create dishes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content(schema = @Schema(implementation = DishResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation")
    })
    public ResponseEntity<DishResponse> createDish(
            @Valid @RequestBody DishRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("");
        DishResponse response = handler.createDish(userId, role, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update dish price and/or description", description = "Updates price and/or description of a dish. Only the restaurant owner can update.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish updated successfully", content = @Content(schema = @Schema(implementation = DishResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "403", description = "User is not the owner"),
        @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<DishResponse> updateDish(
            @PathVariable String id,
            @RequestBody DishUpdateRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("");
        DishResponse response = handler.updateDish(userId, role, id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Update dish active status", description = "Activates or deactivates a dish. Only the restaurant owner can update.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish active status updated successfully", content = @Content(schema = @Schema(implementation = DishResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "403", description = "User is not the owner"),
        @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<DishResponse> updateDishActive(
            @PathVariable String id,
            @Valid @RequestBody com.pragma.plazoleta.application.dto.request.DishActiveUpdateRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (String) auth.getPrincipal();
        String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("");
        DishResponse response = handler.updateDishActive(userId, role, id, dto.getActive());
        return ResponseEntity.ok(response);
    }
} 