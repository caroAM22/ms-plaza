package com.pragma.plazoleta.application.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class RestaurantResponse {
    private UUID id;
    private String name;
    private Long nit;
    private String address;
    private String phone;
    private String logoUrl;
    private UUID ownerId;
} 