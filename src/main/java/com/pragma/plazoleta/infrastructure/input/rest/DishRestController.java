package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.DishRequest;
import com.pragma.plazoleta.application.dto.request.DishUpdateRequest;
import com.pragma.plazoleta.application.dto.request.DishActiveUpdateRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management endpoints")
public class DishRestController {
    private final IDishHandler handler;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER')")
    @Operation(summary = "Create a new dish", description = "Creates a new dish. Only the restaurant owner can create dishes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content(schema = @Schema(implementation = DishResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation")
    })
    public ResponseEntity<DishResponse> createDish(
            @Valid @RequestBody DishRequest dto) {
        DishResponse response = handler.createDish(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
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
        DishResponse response = handler.updateDish(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasAnyRole('OWNER')")
    @Operation(summary = "Update dish active status", description = "Activates or deactivates a dish. Only the restaurant owner can update.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish active status updated successfully", content = @Content(schema = @Schema(implementation = DishResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "403", description = "User is not the owner"),
        @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    public ResponseEntity<DishResponse> updateDishActive(
            @PathVariable String id,
            @Valid @RequestBody DishActiveUpdateRequest dto) {
        DishResponse response = handler.updateDishActive(id, dto);
        return ResponseEntity.ok(response);
    }
} 