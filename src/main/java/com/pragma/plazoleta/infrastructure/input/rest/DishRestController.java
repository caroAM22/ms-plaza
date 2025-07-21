package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.dish.DishRequestDto;
import com.pragma.plazoleta.application.dto.dish.DishResponseDto;
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

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Dishes", description = "Dish management endpoints")
public class DishRestController {
    private final IDishHandler handler;

    @PostMapping
    @Operation(summary = "Create a new dish", description = "Creates a new dish. Only the restaurant owner can create dishes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content(schema = @Schema(implementation = DishResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation")
    })
    public ResponseEntity<DishResponseDto> createDish(
            @RequestHeader("X-USER-ID") String userId,
            @Valid @RequestBody DishRequestDto dto) {
        DishResponseDto response = handler.createDish(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 