package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.response.CategoryResponse;
import com.pragma.plazoleta.application.handler.ICategoryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints to list all categories.")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class CategoryRestController {
    private final ICategoryHandler handler;

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