package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;
import com.pragma.plazoleta.application.handler.ICategoryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints to list all categories or get a category by name (returns id and data)")
public class CategoryRestController {
    private final ICategoryHandler handler;

    @GetMapping(params = "name")
    @Operation(
        summary = "Get a category by name",
        description = "Returns the category (including id) by name, or 404 if not found. Example: /api/v1/categories?name=Seafood"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(schema = @Schema(example = "{\"message\":\"Category not found\"}")))
    })
    public ResponseEntity<Object> getByName(
            @Parameter(description = "Category name", example = "Seafood")
            @RequestParam String name) {
        try {
            CategoryResponse dto = handler.getByName(name);
            return ResponseEntity.ok(dto);
        } catch (com.pragma.plazoleta.domain.exception.DomainException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(
        summary = "List all categories",
        description = "Returns a list of all categories. Example: /api/v1/categories"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of categories", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))))
    })
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> response = handler.getAll();
        return ResponseEntity.ok(response);
    }
} 