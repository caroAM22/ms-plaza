package com.pragma.plazoleta.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class RestaurantRequest {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = ".*[a-zA-Z].*", message = "Name must contain at least one letter")
    private String name;

    @NotNull(message = "NIT is required")
    @Positive(message = "NIT must be positive")
    private Long nit;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    @Size(max = 13, message = "Phone must not exceed 13 characters")
    @Pattern(regexp = "^[+]?\\d{1,13}$", message = "Phone number must contain only digits and optionally start with +")
    private String phone;

    @NotBlank(message = "Logo URL is required")
    private String logoUrl;

    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
} 