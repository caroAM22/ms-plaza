package com.pragma.plazoleta.application.dto.response;

import lombok.Data;

@Data
public class RestaurantResponse {
    private String id;
    private String name;
    private Long nit;
    private String address;
    private String phone;
    private String logoUrl;
    private String ownerId;
} 